package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.repository.RepoFlowScenario
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import javax.inject.Inject

@HiltViewModel
class VMFlowScenario
@Inject constructor(val repo: RepoFlowScenario) :ViewModel()
{
    private val TAG = "VMFlowScenario"
    //store boxes of flow scenario
    private val _boxes = MutableLiveData<ArrayList<FBox>>(arrayListOf<FBox>())
    val boxes: LiveData<ArrayList<FBox>> = _boxes

    //store uuid of the parent box
    private var rootBoxId: String?= null

    init {
        initScenario()
    }

    //initialize a new flow with a default FBoxEventDevice
    private fun initScenario() {
        val currentBoxes = _boxes.value ?: arrayListOf()
        if (currentBoxes.isEmpty()) {
            val fBoxEvent = FBoxEventDevice()
            currentBoxes.add(fBoxEvent)

            _boxes.value = ArrayList(currentBoxes)
        }
    }

    fun setRootBoxId(id: String?) {
        rootBoxId = id
    }

    fun getRootBoxId(): String?= rootBoxId

    /**
     * Configures a box before adding it to the list of boxes
     * It calculate the next available 'id' and 'segId' based on existing boxes
     * and links FBoxAction boxes to their parent box.
     */
    fun configBox(
        fBox: FBox,
        newSegType: LayoutZoomPan.OnBoxActionListener.NewSegType?
    ) {
        viewModelScope.launch {
            val currentBoxes = _boxes.value ?: arrayListOf()
            _boxes.value = ArrayList(
                repo.generateBoxInfo(
                    rootBoxId = rootBoxId,
                    fBox = fBox,
                    newSegType = newSegType,
                    rootBoxes = currentBoxes
            ))
            _boxes.value?.forEach {
                ILogR.D(TAG, "configBox:boxInfo", Gson().toJson(it))
            }
        }
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

    fun getInputsFromParentBox(box: FBox?): ArrayList<Pair<TFInOutType, Int>> {
        return repo.generateInputsFromPreviousBox(box)
    }

    fun generateInputsFromSpecificDevices(
        devType: Int?,
        attrs: IntArray?,
        devMap: HashMap<String?, IntArray>?
    ): ArrayList<Pair<TFInOutType, Int>> {
        return repo.generateInputsFromSpecificDevices(
            devType,
            attrs,
            devMap
        )
    }

    fun generateOutputs(fBox: FBox?): List<Pair<TFInOutType, Int>> {
        return repo.generateOutputs(fBox)
    }
}