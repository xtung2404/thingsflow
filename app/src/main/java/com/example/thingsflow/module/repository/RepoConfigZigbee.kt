package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.rogocore.sdk.callback.CheckDeviceAvailableCallback
import rogo.iot.module.rogocore.sdk.callback.PairZigbeeDeviceCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice
import javax.inject.Inject

class RepoConfigZigbee @Inject constructor() {
    private val TAG = "RepoConfigZigbee"
    val handler = FlowSdk.configZigbeeDeviceHandler()
    var job: Job? = null
    fun isGatewayAvailable(
        gatewayId: String,
        scanningTime: Int,
        callback: RequestResultCallback<Boolean>
    ) {
        job = CoroutineScope(Dispatchers.IO).launch {
            handler.checkZigbeeGatewayAvailable {
                if (it.contentEquals(gatewayId)) {
                    ILogR.D(TAG, "isGatewayAvailable:onDeviceFound", gatewayId)
                    callback.onResult(true)
                    job?.cancel()
                }
            }
            delay(scanningTime * 1000L)
            callback.onResult(false)
            job?.cancel()
        }

    }

    fun checkGatewayAvailable(callback: CheckDeviceAvailableCallback) {
        handler.checkZigbeeGatewayAvailable(callback)
    }

    fun startPairingZigbee(
        devId: String,
        second: Int,
        deviceType: Int = 0,
        callback: PairZigbeeDeviceCallback
    ) {
        handler.startPairZigbeeDevice(
            devId,
            second,
            deviceType,
            callback
        )
    }

    fun syncDeviceToCloud(
        gatewayId: String,
        pairedZigbee: IoTPairedZigbeeDevice,
        label: String,
        groupId: String?= null,
        deviceSubType: Int,
        callback: RequestResultCallback<IoTDevice>
    ) {
        handler.syncDeviceToCloud(
            gatewayId,
            pairedZigbee,
            label,
            groupId,
            deviceSubType,
            callback
        )
    }
}