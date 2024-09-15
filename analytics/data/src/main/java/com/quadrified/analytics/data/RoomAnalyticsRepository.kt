package com.quadrified.analytics.data

import com.quadrified.analytics.domain.AnalyticsRepository
import com.quadrified.analytics.domain.AnalyticsValues
import com.quadrified.core.database.dao.AnalyticsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

// Implementation of "AnalyticsRepository" from "analytics/domain"
class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao
) : AnalyticsRepository {

    override suspend fun getAnalyticsValues(): AnalyticsValues {
        // Reads directly from the RunDatabase because it is single source of truth
        return withContext(Dispatchers.IO) {
            val totalDistance = async { analyticsDao.getTotalDistance() }
            val totalTimeMillis = async { analyticsDao.getTotalTimeRun() }
            val maxRunSpeed = async { analyticsDao.getMaxRunSpeed() }
            val avgDistancePerRun = async { analyticsDao.avgDistancePerRun() }
            val avgPacePerRun = async { analyticsDao.avgPacePerRun() }

            AnalyticsValues(
                totalDistanceRun = totalDistance.await(),
                totalTimeRun = totalTimeMillis.await().milliseconds,
                fastestEverRun = maxRunSpeed.await(),
                avgDistancePerRun = avgDistancePerRun.await(),
                avgPacePerRun = avgPacePerRun.await()
            )
        }
    }
}