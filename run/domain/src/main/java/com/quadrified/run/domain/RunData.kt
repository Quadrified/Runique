package com.quadrified.run.domain

import com.quadrified.core.domain.location.LocationTimestamp
import kotlin.time.Duration

// Run data only used in Run
data class RunData(
    val distanceMeters: Int = 0,
    val pace: Duration = Duration.ZERO,
    val locations: List<List<LocationTimestamp>> = emptyList()
)
