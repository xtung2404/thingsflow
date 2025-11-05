package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.callback.SuccessRequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.platform.entity.IoTWifiInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.CheckDeviceAvailableCallback
import rogo.iot.module.rogocore.sdk.callback.DiscoverySmartDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.PairZigbeeDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SetupWileDirectDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice
import javax.inject.Inject

class RepoFlowBinding @Inject constructor() {
    private val TAG = "RepoFlowBinding"
    val handler = FlowSdk.flowHandler()

    fun createFlowBinding(
        devId: String,
        sceneId: String,
        label: String,
        callback: SuccessStatusCallback
    ) {
        handler.createFlowBinding(
            devId,
            sceneId,
            "",
            label,
            callback
        )
    }

    fun bindBoxes(
        devId: String,
        bindingId: String,
        boxes: ArrayList<FBox?>,
        callback: SuccessStatusCallback
    ) {
        handler.bindBoxes(
            devId,
            bindingId,
            boxes,
            callback
        )
    }
}