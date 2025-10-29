package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.model.ConfigZigbeeDeviceModel
import com.example.thingsflow.module.repository.RepoConfigWileDirect
import com.example.thingsflow.module.repository.RepoConfigZigbee
import com.example.thingsflow.module.repository.RepoDevice
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
class VMDevice
@Inject constructor(val repo: RepoDevice) :ViewModel()
{
    private val TAG = "VMDevice"

    fun getAll(): List<IoTDevice?> = repo.getAll()

}