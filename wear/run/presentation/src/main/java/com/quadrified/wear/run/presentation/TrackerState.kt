package com.quadrified.wear.run.presentation

import kotlin.time.Duration

data class TrackerState(
    val elapsedDuration: Duration = Duration.ZERO,
    val distanceMeter: Int = 0,
    val heartRate: Int = 0,
    // To see if wear app can start tracking
    // No permission etc
    val isTrackable: Boolean = false,
    val hasStartedRunning: Boolean = false,
    // Active connection to mobile device
    val isConnectedPhoneNearby: Boolean = false,
    val isRunActive: Boolean = false,
    val canTrackHeartRate: Boolean = false
)
