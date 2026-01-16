package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.RepoFlowBase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import javax.inject.Inject

@HiltViewModel
class VMFlowBinding
@Inject constructor(private val repo: RepoFlowBase) : VMFlowBase(repo)
{
    private val TAG = "VMFlowBinding"
    private var selectedFlowScene: String?= null
    private var selectedFBox: FBox?= null
    private val selectedGateways: HashMap<String?, IntArray> = hashMapOf()

    fun setBoxes(boxes: ArrayList<FBox>) {
        _boxes.value = boxes
    }
    fun setSelectedFlowScene(sceneId: String) {
        selectedFlowScene = sceneId
    }

    fun getSelectedFlowSceneId(): String? {
        return selectedFlowScene
    }

    fun setSelectedBox(fBox: FBox?) {
        selectedFBox = fBox
    }

    fun getSelectedBox(): FBox? {
        return selectedFBox
    }

    fun setSelectedGateways(devices: HashMap<String?, IntArray>) {
        viewModelScope.launch {
            selectedGateways.clear()
            selectedGateways.putAll(devices)
        }
    }

    fun getSelectedGateways(): HashMap<String?, IntArray> {
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
        boxes: ArrayList<FBox?>,
        callback: SuccessStatusCallback
    ) {
        viewModelScope.launch {
            repo.bindBoxes(
                devId,
                bindingId,
                boxes,
                callback
            )
        }
    }

}