package com.quadrified.wear.run.data

import com.quadrified.core.connectivity.domain.DeviceNode
import com.quadrified.core.connectivity.domain.DeviceType
import com.quadrified.core.connectivity.domain.NodeDiscovery
import com.quadrified.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Implementation of "wear/domain/PhoneConnector"
 */
class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope
) : PhoneConnector {
    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedNode: StateFlow<DeviceNode?> = _connectedNode.asStateFlow()

    // Flow triggered when new message (button press etc) arrives from Phone to Watch
    val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.WATCH)
        .onEach { connectedNodes ->
            val node = connectedNodes.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedNode.value = node
            }
        }
        .launchIn(applicationScope)
}