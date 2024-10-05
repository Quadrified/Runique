package com.quadrified.run.domain

import com.quadrified.core.connectivity.domain.DeviceNode
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementation in "run/data/connectivity"
 */
interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>

    fun setIsTrackable(isTrackable: Boolean)
}