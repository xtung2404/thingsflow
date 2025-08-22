package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.callback.SuccessRequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.platform.entity.IoTWifiInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.DiscoverySmartDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SetupWileDirectDeviceCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import javax.inject.Inject

class RepoConfigWileDirect @Inject constructor() {
    val handler = FlowSdk.configWileDirectDeviceHandler()
    var discoveryJob: Job?= null
    fun discovery(
        scanningTime: Long,
        callback: ScanningIoTDeviceCallback
    ) {
        val time: Long = scanningTime * 1000
        var isEmpty: Boolean = true
        discoveryJob = CoroutineScope(Dispatchers.IO).launch {
            SmartSdk.discoverySmartDeviceHandler()
                .discovery(
                    object : DiscoverySmartDeviceCallback {
                        override fun onSmartDeviceFound(p0: IoTDirectDeviceInfo?) {
                            p0?.let {
                                isEmpty = false
                                if (it.typeConnect == IoTDirectDeviceInfo.TypeConnect.WILEDIRECTLAN) {
                                    callback.onDeviceFound(it)
                                }
                            }
                        }

                        override fun onSmartDeviceRemove(p0: String?) {

                        }
                    }
                )
            delay(time)
            stopDiscovery()
            if (isEmpty) {
                callback.onTimeOut()
            }
        }
    }

    fun stopDiscovery() {
        SmartSdk.discoverySmartDeviceHandler().stopDiscovery()
    }

    fun cancelDiscovery() {
        discoveryJob?.cancel()
        stopDiscovery()
    }

    fun connectAndIdentifyDevice(
        device: IoTDirectDeviceInfo,
        callback: SetupWileDirectDeviceCallback
    ) {
        cancelDiscovery()
        handler.connectAndIdentifyDevice(
            device,
            callback
        )
    }

    fun scanWiFi(
        callback: RequestCallback<Collection<IoTWifiInfo>>
    ) {
        handler.scanWifi(
            8,
            callback
        )
    }

    fun requestConnectWifiNetwork(
        ssid: String,
        pwd: String,
        callback: SuccessRequestCallback
    ) {
        handler.requestConnectWifiNetwork(
                0,
                ssid,
                pwd,
                true,
                callback
            )
    }

    fun setupAndSyncDeviceToCloud(
        label: String,
        selectedGroup: String?,
        deviceSubType: Int,
        callback: RequestCallback<IoTDevice>
    ) {
        handler.setupAndSyncDeviceToCloud(
            label,
            selectedGroup,
            deviceSubType,
            callback
        )
    }
}