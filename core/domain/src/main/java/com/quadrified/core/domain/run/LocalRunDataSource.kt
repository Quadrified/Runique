package com.quadrified.core.domain.run

import com.quadrified.core.domain.util.DataError
import com.quadrified.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

// Used in Repository to combine data sources and to use project wise
// Same as "RunDao"

typealias RunId = String

interface LocalRunDataSource {
    suspend fun upsertRun(run: Run): Result<RunId, DataError.Local>

    suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local>

    fun getRuns(): Flow<List<Run>>

    suspend fun deleteRun(id: String)

    suspend fun deleteAllRuns()
}