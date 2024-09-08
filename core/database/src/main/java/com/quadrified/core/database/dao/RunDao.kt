package com.quadrified.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.quadrified.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    // Upsert single run
    @Upsert
    suspend fun upsertRun(run: RunEntity)

    // Upsert multiple runs
    @Upsert
    suspend fun upsertRuns(runs: List<RunEntity>)

    // Read all runs
    @Query("SELECT * FROM runentity ORDER BY datTimeUtc DESC")
    fun getRuns(): Flow<List<RunEntity>>

    // Delete runs by id
    @Query("DELETE FROM runentity WHERE id=:id")
    suspend fun deleteRun(id: String)

    // Delete all
    @Query("DELETE FROM runentity")
    suspend fun deleteAllRuns()
}