package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.model.ConfigZigbeeDeviceModel
import com.example.thingsflow.module.repository.RepoConfigWileDirect
import com.example.thingsflow.module.repository.RepoConfigZigbee
import com.example.thingsflow.module.repository.RepoDevice
import com.example.thingsflow.module.repository.RepoFlowBinding
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.flowcommon.box.FBox
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
class VMFlowBinding
@Inject constructor(val repo: RepoFlowBinding) :ViewModel()
{
    private val TAG = "VMFlowBinding"
    private var selectedFlowScene: String?= null
    private val selectedGateways: ArrayList<IoTDevice> = arrayListOf()


    fun setSelectedFlowScene(sceneId: String) {
        selectedFlowScene = sceneId
    }

    fun getSelectedFlowSceneId(): String? {
        return selectedFlowScene
    }

    fun setSelectedGateways(nodes: List<IoTDevice>) {
        viewModelScope.launch {
            selectedGateways.clear()
            selectedGateways.addAll(nodes)
        }
    }

    fun getSelectedDevices(): ArrayList<IoTDevice> {
        return selectedGateways
    }
    fun createFlowBinding(
        devId: String,
        sceneId: String,
        label: String,
        callback: SuccessStatusCallback
    ) {
        viewModelScope.launch {
            repo.createFlowBinding(
                devId,
                sceneId,
                label,
                callback
            )
        }
    }

    fun bindBoxes(
        devId: String,
        bindingId: String,
        eventBoxes: ArrayList<FBox?>,
        callback: SuccessStatusCallback
    ) {
        viewModelScope.launch {
            repo.bindBoxes(
                devId,
                bindingId,
                eventBoxes,
                callback
            )
        }
    }

}