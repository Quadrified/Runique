package com.quadrified.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quadrified.core.database.dao.RunPendingSyncDao
import com.quadrified.core.database.mappers.toRun
import com.quadrified.core.domain.run.RemoteRunDataSource
import com.quadrified.core.domain.util.Result

// To upsert run to REMOTE when created LOCALLY
class CreateRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        // Fetching pending run data
        val pendingRunId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        val pendingRunEntity = pendingSyncDao.getRunPendingSyncEntity(pendingRunId)
            ?: return Result.failure()

        // Converting to domain model
        val run = pendingRunEntity.run.toRun()

        return when (val result =
            remoteRunDataSource.postRun(run, pendingRunEntity.mapPictureBytes)) {
            is com.quadrified.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }

            is com.quadrified.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}