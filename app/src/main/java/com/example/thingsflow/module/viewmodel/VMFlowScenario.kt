package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.repository.RepoFlowScenario
import com.example.thingsflow.ui.adapter.AdapterItem
import com.example.thingsflow.ui.customview.LayoutZoomPan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.type.FBoxType
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

    fun getInputsFromParentBox(box: FBox?): List<TFInputBoxValue?> {
        return repo.generateInputsFromPreviousBox(box)
    }

    fun generateInputsFromSpecificDevices(
        devType: Int?,
        attrs: IntArray?,
        devMap: HashMap<String?, IntArray>?
    ): List<TFInputBoxValue?> {
        return repo.generateInputsFromSpecificDevices(
            devType,
            attrs,
            devMap
        )
    }

    fun groupInputs(boxType: Int, inputs: List<TFInputBoxValue?>): MutableList<AdapterItem> {
        val displayList = mutableListOf<AdapterItem>()
        when(boxType) {
            FBoxType.ACT_CALL_HTTP -> {
                inputs?.forEach { input ->
                    displayList.add(AdapterItem.ContentItem(input))
                }
            }

            FBoxType.EVT_FROM_DEVICE -> {
                val groupedInputsByDevice = inputs.groupBy { it?.devId }
                groupedInputsByDevice.forEach { (devId, values) ->
                    val device = FlowSdk.deviceHandler().get(devId)
                    device?.let {
                        val groupedInputsByElm = values.groupBy { it?.elm }
                        groupedInputsByElm.forEach { (elm, values) ->
                            val elmInfo = device.elementInfos[elm]
                            elmInfo?.let {
                                if (device.elementIds.size != 1) {
                                    if (elmInfo.label.isNullOrEmpty()) {
                                        displayList.add(AdapterItem.HeaderItem("Nút $elm"))
                                    } else {
                                        displayList.add(AdapterItem.HeaderItem(elmInfo.label))
                                    }
                                }
                            }
                            values.forEach { value ->
                                displayList.add(AdapterItem.ContentItem(value))
                            }
                        }
                    }
                }
            }
        }

        return displayList
    }

    fun generateOutputs(fBox: FBox?): List<TFInputBoxValue> {
        return repo.generateOutputs(fBox)
    }

    fun createFlowScene(
        flowSceneId: String,
        devId: String,
        flowSceneLabel: String
    ) {
        repo.createFlowScene(flowSceneId, devId, flowSceneLabel)
    }

    fun createSceneBoxes(
        flowSceneId: String,
        devId: String,
        boxes: ArrayList<FBox>
    ) {
        repo.createSceneBoxes(
            flowSceneId,
            devId,
            boxes
        )
    }
}