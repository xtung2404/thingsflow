package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.callback.SuccessRequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.platform.entity.IoTWifiInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.CheckDeviceAvailableCallback
import rogo.iot.module.rogocore.sdk.callback.DiscoverySmartDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.PairZigbeeDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SetupWileDirectDeviceCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice
import javax.inject.Inject

class RepoConfigZigbee @Inject constructor() {
    val handler = FlowSdk.configZigbeeDeviceHandler()

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
        callback: RequestCallback<IoTDevice>
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