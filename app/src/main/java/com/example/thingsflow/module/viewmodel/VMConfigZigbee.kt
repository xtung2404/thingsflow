package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.model.ConfigZigbeeDeviceModel
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
    private val TAG = "VMConfigZigbee"
    private var scannedZigbeeDevices: ArrayList<IoTPairedZigbeeDevice> = arrayListOf()
    private var syncingZigbeeDevices: ArrayList<ConfigZigbeeDeviceModel> = arrayListOf()
    private var pairedZigbeeDevice: IoTPairedZigbeeDevice?= null
    private var selectedGateway: String?= null

    fun isGatewayAvailable(
        gatewayId: String,
        scanningTime: Int,
        callback: RequestCallback<Boolean>
    ) {
        viewModelScope.launch {
            repo.isGatewayAvailable(
                gatewayId,
                scanningTime,
                callback
            )
        }
    }
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
            selectedGateway = devId
            repo.startPairingZigbee(
                devId,
                second,
                deviceType,
                callback
            )
        }
    }

    fun setScannedZigbeeDevices(scannedDevices: List<IoTPairedZigbeeDevice>) {
        viewModelScope.launch {
            scannedZigbeeDevices.clear()
            scannedZigbeeDevices.addAll(scannedDevices)
        }
    }

    fun setSyncingZigbeeDevices(scannedDevices: List<ConfigZigbeeDeviceModel>) {
        viewModelScope.launch {
            syncingZigbeeDevices.clear()
            syncingZigbeeDevices.addAll(scannedDevices)
        }
    }

    fun getScannedZigbeeDevices(): ArrayList<IoTPairedZigbeeDevice> = scannedZigbeeDevices
    fun getSyncingZigbeeDevices(): ArrayList<ConfigZigbeeDeviceModel> = syncingZigbeeDevices
    fun getSelectedGateway(): String? = selectedGateway
    fun syncDeviceToCloud(
        gatewayId: String,
        pairedZigbeeDevice: IoTPairedZigbeeDevice,
        label: String,
        groupId: String?= null,
        deviceSubType: Int,
        callback: RequestCallback<IoTDevice>
    ) {
        viewModelScope.launch {
            repo.syncDeviceToCloud(
                gatewayId,
                pairedZigbeeDevice,
                label,
                groupId,
                deviceSubType,
                callback
            )
        }
    }

}