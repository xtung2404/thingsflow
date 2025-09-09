package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.RepoConfigWileDirect
import com.example.thingsflow.module.repository.RepoConfigZigbee
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.callback.SuccessRequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.platform.entity.IoTNetworkConnectivity
import rogo.iot.module.platform.entity.IoTWifiInfo
import rogo.iot.module.rogocore.sdk.callback.CheckDeviceAvailableCallback
import rogo.iot.module.rogocore.sdk.callback.PairZigbeeDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SetupWileDirectDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice
import javax.inject.Inject

@HiltViewModel
class VMConfigZigbee
@Inject constructor(val repo: RepoConfigZigbee) :ViewModel()
{
    private val TAG = "ConfigWileDirectViewModel"
    private var identifiedDevice: IoTDirectDeviceInfo?= null
    fun checkGatewayAvailable(
        callback: CheckDeviceAvailableCallback
    ) {
        viewModelScope.launch {
            repo.checkGatewayAvailable(
                callback
            )
        }
    }

    fun startPairingDevice(
        devId: String,
        second: Int,
        deviceType: Int = 0,
        callback: PairZigbeeDeviceCallback
    ) {
        viewModelScope.launch {
            repo.startPairingZigbee(
                devId,
                second,
                deviceType,
                callback
            )
        }
    }

    fun syncDeviceToCloud(
        gatewayId: String,
        pairedZigbee: IoTPairedZigbeeDevice,
        label: String,
        groupId: String?= null,
        deviceSubType: Int,
        callback: RequestCallback<IoTDevice>
    ) {
        viewModelScope.launch {
            repo.syncDeviceToCloud(
                gatewayId,
                pairedZigbee,
                label,
                groupId,
                deviceSubType,
                callback
            )
        }
    }

}