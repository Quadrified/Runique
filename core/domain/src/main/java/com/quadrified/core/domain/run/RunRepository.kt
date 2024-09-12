package com.quadrified.core.domain.run

import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {

    // From LocalDB
    fun getRuns(): Flow<List<Run>>

    // From Remote
    suspend fun fetchRuns(): EmptyResult<DataError>

    suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError>

    suspend fun deleteRun(id: RunId)

    suspend fun syncPendingRuns()
}