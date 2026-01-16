package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.repository.RepoFlowBase
import com.example.thingsflow.ui.adapter.AdapterItem
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.type.FBoxType
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

open class VMFlowBase constructor(private val repo: RepoFlowBase): ViewModel() {
    protected val _boxes = MutableLiveData<ArrayList<FBox>>(arrayListOf<FBox>())
    val boxes: LiveData<ArrayList<FBox>> = _boxes

    /**
     * update info of an existing box
     */
    fun updateBoxInfo(updatedBox: FBox?) {
        if (updatedBox == null) return

        val currentBoxes = _boxes.value ?: return

        val newBoxes = currentBoxes.map { existingBox ->
            if (existingBox.id == updatedBox.id) {
                updatedBox
            } else {
                existingBox
            }
        }

        _boxes.value = ArrayList(newBoxes)
    }

    /**
     * get inputs from parent box
     */
    fun getInputsFromParentBox(box: FBox?): List<TFInputBoxValue?> {
        return repo.generateInputsFromPreviousBox(box)
    }

    /**
     * get inputs from a specific device
     */
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
                inputs.forEach { input ->
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

    fun getOutputs(fBox: FBox?): List<TFInputBoxValue> {
        return repo.generateOutputs(fBox)
    }
}