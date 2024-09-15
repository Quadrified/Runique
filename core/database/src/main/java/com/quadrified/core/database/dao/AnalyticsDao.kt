package com.quadrified.core.database.dao

import androidx.room.Dao
import androidx.room.Query

// Reads directly from the RunDatabase because it is single source of truth

@Dao
interface AnalyticsDao {

    @Query("SELECT SUM(distanceMeters) FROM runentity")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(durationMillis) FROM runentity")
    suspend fun getTotalTimeRun(): Long

    @Query("SELECT MAX(maxSpeedKmh) FROM runentity")
    suspend fun getMaxRunSpeed(): Double

    @Query("SELECT AVG(distanceMeters) FROM runentity")
    suspend fun avgDistancePerRun(): Double

    @Query("SELECT AVG((durationMillis/ 60000.0) / (distanceMeters / 1000.0)) FROM runentity")
    suspend fun avgPacePerRun(): Double
}