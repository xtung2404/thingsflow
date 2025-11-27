package com.example.thingsflow.module.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
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
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice
import javax.inject.Inject

class RepoFlowScenario @Inject constructor() {
    private val TAG = "RepoFlowScenario"

    val handler = FlowSdk.flowHandler()




}