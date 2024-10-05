package com.quadrified.core.connectivity.domain

import kotlinx.coroutines.flow.Flow

/**
 * Node => Remote Device to discover
 * Implementation in "core/connectivity/data"
 */
interface NodeDiscovery {

    fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>>
}