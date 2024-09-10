package com.quadrified.core.data.run

import com.quadrified.core.domain.run.LocalRunDataSource
import com.quadrified.core.domain.run.RemoteRunDataSource
import com.quadrified.core.domain.run.Run
import com.quadrified.core.domain.run.RunId
import com.quadrified.core.domain.run.RunRepository
import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.EmptyResult
import com.quadrified.core.domain.util.Result
import com.quadrified.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

// Implementation of "RunRepository" from "core/domain/run"
class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope
) : RunRepository {
    // From LocalDB
    override fun getRuns(): Flow<List<Run>> {
        // getRuns() interface from "core.domain.run"
        // getRuns() implementation in "RoomLocalRunDataSource" from "core/database"
        return localRunDataSource.getRuns()
    }

    /**
     * LocalDB to be single source of truth
     */
    // From Remote
    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                // Different from VM where fetchRuns is called
                // applicationScope lives longer
                applicationScope.async {
                    // Fetching runs and updating it in localDB
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }

}