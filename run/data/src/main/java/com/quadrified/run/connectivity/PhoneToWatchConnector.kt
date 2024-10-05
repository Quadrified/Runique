package com.quadrified.run.connectivity

import com.quadrified.core.connectivity.domain.DeviceNode
import com.quadrified.core.connectivity.domain.DeviceType
import com.quadrified.core.connectivity.domain.NodeDiscovery
import com.quadrified.run.domain.WatchConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Implementation of "run/domain/WatchConnector"
 */
class PhoneToWatchConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope
) : WatchConnector {
    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice = _connectedNode.asStateFlow()

    private val isTrackable = MutableStateFlow(false)

    // Flow triggered when new message (button press etc) arrives from Watch To Phone
    val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.PHONE)
        .onEach { connectedDevices ->
            // node => device we want to connect to
            val node = connectedDevices.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedNode.value = node
            }
        }
        .launchIn(applicationScope)

    override fun setIsTrackable(isTrackable: Boolean) {
        // this.isTrackable => isTrackable = MutableStateFlow(false)
        this.isTrackable.value = isTrackable
    }

}