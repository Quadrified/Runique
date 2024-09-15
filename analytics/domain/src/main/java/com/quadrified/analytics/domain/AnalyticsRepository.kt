package com.quadrified.analytics.domain

// Implementation in "analytics/data/RoomAnalyticsRepository"
interface AnalyticsRepository {

    suspend fun getAnalyticsValues(): AnalyticsValues
}