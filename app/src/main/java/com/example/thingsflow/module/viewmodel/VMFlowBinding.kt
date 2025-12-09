package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val selectedGateways: HashMap<String?, IntArray> = hashMapOf()

    private val _boxes = MutableLiveData<ArrayList<FBox>>(arrayListOf<FBox>())
    val boxes: LiveData<ArrayList<FBox>> = _boxes

    fun setBoxes(boxes: ArrayList<FBox>) {
        _boxes.value = boxes
    }
    fun setSelectedFlowScene(sceneId: String) {
        selectedFlowScene = sceneId
    }

    fun getSelectedFlowSceneId(): String? {
        return selectedFlowScene
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

    fun updateBox(updatedBox: FBox?) {
        if (updatedBox == null) return

        val currentBoxes = _boxes.value ?: return

        val newBoxes = currentBoxes.map { existingBox ->
            if (existingBox.id == updatedBox.id) {
                updatedBox // Thay thế bằng box đã được cập nhật
            } else {
                existingBox // Giữ nguyên box cũ
            }
        }

        _boxes.value = ArrayList(newBoxes)
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