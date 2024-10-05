package com.quadrified.core.connectivity.data

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import com.quadrified.core.connectivity.domain.DeviceNode
import com.quadrified.core.connectivity.domain.DeviceType
import com.quadrified.core.connectivity.domain.NodeDiscovery
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementation of NodeDiscovery from "core/connectivity/domain"
 */
class WearNodeDiscovery(
    context: Context
) : NodeDiscovery {

    private val capabilityClient = Wearable.getCapabilityClient(context)

    override fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>> {
        return callbackFlow {
            // Remote Capability => String that other device has to have to count as a device with Runique App installed
            val remoteCapability = when (localDeviceType) {
                // If local device is PHONE => remote is WEAR
                DeviceType.PHONE -> "runique_wear_app"
                DeviceType.WATCH -> "runique_phone_app"
            }

            try {
                val capability = capabilityClient
                    .getCapability(remoteCapability, CapabilityClient.FILTER_REACHABLE)
                    .await()

                val connectedDevices = capability.nodes.map { it.toDeviceNode() }.toSet()

                send(connectedDevices)
            } catch (e: ApiException) {
                awaitClose()
                return@callbackFlow
            }

            val listener: (CapabilityInfo) -> Unit = {
                trySend(it.nodes.map { it.toDeviceNode() }.toSet())
            }

            capabilityClient.addListener(listener, remoteCapability)

            awaitClose {
                capabilityClient.removeListener(listener)
            }
        }
    }
}