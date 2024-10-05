package com.quadrified.wear.run.domain

import com.quadrified.core.connectivity.domain.DeviceNode
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementation in "wear/data/connectivity"
 */
interface PhoneConnector {
    val connectedNode: StateFlow<DeviceNode?>
}