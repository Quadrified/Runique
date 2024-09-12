package com.quadrified.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quadrified.core.database.dao.RunPendingSyncDao
import com.quadrified.core.domain.run.RemoteRunDataSource

// For deleting a deleted run to not sync a LOCALLY deleted run
class DeleteRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        // Run id we want to delete, patched to the bundle
        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()

        return when (val result = remoteRunDataSource.deleteRun(runId)) {
            is com.quadrified.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }

            is com.quadrified.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteDeletedRunSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}