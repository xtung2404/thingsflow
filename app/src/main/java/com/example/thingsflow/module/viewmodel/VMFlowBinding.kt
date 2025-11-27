package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.RepoFlowBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
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