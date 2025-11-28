package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.RepoConfigWileDirect
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.base.callback.RequestStatusCallback
import rogo.iot.module.base.entity.IoTModelSmartConfig
import rogo.iot.module.base.entity.IoTNetworkConnectivity
import rogo.iot.module.base.entity.IoTSoftwareInfo
import rogo.iot.module.base.entity.IoTWifiInfo
import rogo.iot.module.rogocore.sdk.callback.SetupWileDirectDeviceCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import javax.inject.Inject

@HiltViewModel
class VMConfigWileDirect
@Inject constructor(val repo: RepoConfigWileDirect) :ViewModel()
{
    private val TAG = "ConfigWileDirectViewModel"
    private var identifiedDevice: IoTModelSmartConfig?= null
    private var supportedConnecitivities: HashMap<IoTNetworkConnectivity, Boolean> = hashMapOf()
    fun discovery(
        scanningTime: Long,
        callback: ScanningIoTDeviceCallback
    ) {
        viewModelScope.launch {
            repo.discovery(
                scanningTime,
                callback
            )
        }
    }

    fun connectAndIdentifyDevice(
        device: IoTModelSmartConfig,
        callback: RequestResultCallback<HashMap<IoTNetworkConnectivity, Boolean>>
    ) {
        identifiedDevice = device
        viewModelScope.launch {
            repo.connectAndIdentifyDevice(
                device,
                object : SetupWileDirectDeviceCallback {
//                    override fun onDeviceIdentifiedAndReadySetup(
//                        mac: String?,
//                        firmwareVersion: String?,
//                        networkConnectivities: MutableCollection<IoTNetworkConnectivity>?
//                    ) {
//
//                    }

                    override fun onDeviceIdentifiedAndReadySetup(
                        mac: String?,
                        softwareInfo: IoTSoftwareInfo?,
                        networkConnectivities: Collection<IoTNetworkConnectivity?>?
                    ) {
                        networkConnectivities?.forEach {
                            ILogR.D(TAG, "connectAndIdentifyDevice: connectivities", Gson().toJson(it))
                        }
                        networkConnectivities?.let {
                            supportedConnecitivities = networkConnectivities.associateWith { false } as HashMap<IoTNetworkConnectivity, Boolean>
                            callback.onResult(supportedConnecitivities)
                        }
                    }

                    override fun onProgress(p0: Int, p1: String?) {

                    }

                    override fun onSetupFailure(
                        p0: Int,
                        p1: Int,
                        p2: String?
                    ) {
                        ILogR.D(TAG, "connectAndIdentifyDevice", p0, p1)
                        callback.onError(p0)
                    }
                }
            )
        }
    }

    fun cancelDiscovery() {
        viewModelScope.launch {
            repo.cancelDiscovery()
        }
    }

    fun scanWiFi(
        callback: RequestResultCallback<Collection<IoTWifiInfo>>
    ) {
        viewModelScope.launch {
            repo.scanWiFi(callback)
        }
    }

    fun requestConnectWifiNetwork(
        ssid: String,
        pwd: String,
        callback: RequestStatusCallback
    ) {
        viewModelScope.launch {
            repo.requestConnectWifiNetwork(
                ssid,
                pwd,
                callback
            )
        }
    }

    fun setupAndSyncDeviceToCloud(
        label: String,
        selectedGroup: String?,
        deviceSubType: Int,
        callback: RequestResultCallback<IoTDevice>
    ) {
        viewModelScope.launch {
            repo.setupAndSyncDeviceToCloud(
                label,
                selectedGroup,
                deviceSubType,
                callback
            )
        }
    }
    fun getIdentifiedDevice(): IoTModelSmartConfig? = identifiedDevice

    fun getSupportedConnectivities(): HashMap<IoTNetworkConnectivity, Boolean> = supportedConnecitivities
}