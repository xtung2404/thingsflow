package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import com.example.thingflowsdk.core.base.entity.TFFlowScenario
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.ui.customview.LayoutZoomPan
import kotlinx.coroutines.flow.flow
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.define.FJsonField
import rogo.iot.module.flowcommon.type.FInputValueType
import rogo.iot.module.flowcommon.value.FInputValue
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import javax.inject.Inject

class RepoFlowScenario @Inject constructor() {
    private val TAG = "RepoFlowScenario"

    val handler = FlowSdk.flowScenarioHandler()

    /**
     * configure information for a new added box
     * @param rootBoxId: the id of the parent box
     * @param fBox: the box to be configured
     * @param newSegType: the type of the new segment that the new added box belongs to
     * @param rootBoxes: the list of existing boxes
     */
    fun generateBoxInfo(
        rootBoxId: String?,
        fBox: FBox,
        newSegType: LayoutZoomPan.OnBoxActionListener.NewSegType?,
        rootBoxes: ArrayList<FBox>
    ): ArrayList<FBox> {
        val currentBoxes = rootBoxes
        var highestBoxId: Int = 0
        var highestSegId: Int = 0
        // Iterate through existing boxes to find the maximum 'id' and 'segId'
        currentBoxes.forEach { currentBox ->
            val id = currentBox.id.toInt()
            if (id > highestBoxId) {
                highestBoxId = id
            }
            if (currentBox is FBoxAction) {
                val segId: Int = currentBox.segId.toInt()
                if (segId > highestSegId) {
                    highestSegId = segId
                }
            }
        }

        when (fBox) {
            is FBoxEvent -> {
                // set ID for box event
                fBox.id = (highestBoxId + 1).toString()
            }

            is FBoxAction -> {
                //set the id of next segment for the first event box
                if (currentBoxes.size == 1) {
                    if (currentBoxes[0] is FBoxEvent) {
                        (currentBoxes[0] as FBoxEvent).targetSegId = (highestSegId + 1).toString()
                    }
                }
                // set ID for the current box action
                fBox.id = (highestBoxId + 1).toString()
                // set segmentID for the current box action
                fBox.segId = (highestSegId + 1).toString()
                // set the id of the parent box for the current box
                fBox.rootId = rootBoxId
                fBox.positiveSegId = ""
                fBox.negativeSegId = ""
                val rootBox = currentBoxes.find { it.id == rootBoxId }
                // determine if the current box belongs to positive segment or negative segment of the parent box
                if (rootBox != null && rootBox is FBoxAction) {
                    when (newSegType) {
                        LayoutZoomPan.OnBoxActionListener.NewSegType.DEFAULT,
                        LayoutZoomPan.OnBoxActionListener.NewSegType.POSITIVE -> {
                            rootBox.positiveSegId = fBox.segId
                        }

                        LayoutZoomPan.OnBoxActionListener.NewSegType.NEGATIVE -> {
                            rootBox.negativeSegId = fBox.segId
                        }

                        else -> {

                        }
                    }
                }
            }
        }
        currentBoxes.add(fBox)
        return currentBoxes
    }

    /**
     * generate inputs from previous box
     * @param fBox: the previous box
     */
    fun generateInputsFromPreviousBox(fBox: FBox?): ArrayList<TFInputBoxValue> {
        val availableInputs: ArrayList<TFInputBoxValue> = arrayListOf()
        when (fBox) {
            is FBoxEventDevice -> {
                if (!fBox.devId.isNullOrEmpty()) {
                    availableInputs.addAll(generateInputsFromDevice(fBox.devId, fBox.elms))
                }
            }

            is FBoxActionCallHttp -> {
                availableInputs.addAll(generateInputsFromCallHttp(fBox.jsonFields))
            }
        }
        return availableInputs
    }

    /**
     * generate inputs from specific devices
     * @param devType: ```Int``` type of device
     * @param attrs: ```IntArray``` list of attributes
     * @param devMap: map of selected elements with the uuid of the device. key: uuid of the device, value: selected elements of devices
     */
    fun generateInputsFromSpecificDevices(
        devType: Int?,
        attrs: IntArray?,
        devMap: HashMap<String?, IntArray>?
    ): ArrayList<TFInputBoxValue> {
        val availableInputs: ArrayList<TFInputBoxValue> = arrayListOf()
        devMap?.let {
            if (!devMap.isEmpty()) {
                devMap.forEach { deviceEntry ->
                    availableInputs.addAll(generateInputsFromDevice(deviceEntry.key, deviceEntry.value))
                }

            }
        }

        return availableInputs
    }

    /**
     * generate state inputs basing on information of a device
     */
    private fun generateInputsFromDevice(devId: String?, elms: IntArray?): ArrayList<TFInputBoxValue> {
        val availableInputs = arrayListOf<TFInputBoxValue>()
        val device = FlowSdk.deviceHandler().get(devId)
        device?.let {
            when (device.elementIds.size) {
                1 -> {
                    device.features.forEach { feature ->
                        val inputValue = FInputValue()
                        inputValue.setIntegerValue(feature)
                        availableInputs.add(
                            TFInputBoxValue(
                                device.uuid,
                                device.elementIds[0],
                                TFInOutType.PAYLOAD_STATE,
                                inputValue
                            )
                        )
                    }
                }

                else -> {
                    device.elementInfos.forEach { elmInfo ->
                        if (elms?.contains(elmInfo.key) == true) {
                            if (elmInfo.value.attrs != null && elmInfo.value.attrs.isNotEmpty()) {
                                elmInfo.value.attrs.forEach { feature ->
                                    val inputValue = FInputValue()
                                    inputValue.setIntegerValue(feature)
                                    availableInputs.add(
                                        TFInputBoxValue(
                                            device.uuid,
                                            elmInfo.key,
                                            TFInOutType.PAYLOAD_STATE,
                                            inputValue
                                        )
                                    )
                                }
                            } else {
                                device.features.forEach { feature ->
                                    val inputValue = FInputValue()
                                    inputValue.setIntegerValue(feature)
                                    availableInputs.add(
                                        TFInputBoxValue(
                                            device.uuid,
                                            elmInfo.key,
                                            TFInOutType.PAYLOAD_STATE,
                                            inputValue
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        return availableInputs
    }

    private fun generateInputsFromCallHttp(jsonFields: Array<FJsonField>): ArrayList<TFInputBoxValue> {
        val availableInputs = arrayListOf<TFInputBoxValue>()
        jsonFields.forEach { field ->
            if (field.type == FInputValueType.OBJECT) {
                availableInputs.addAll(generateInputsFromCallHttp(field.fields))
            } else {
                val inputValue = FInputValue()
                inputValue.setValue(field.type, field.jsonPath)
                availableInputs.add(
                    TFInputBoxValue(
                        null,
                        null,
                        TFInOutType.JSON_FIELD,
                        inputValue
                    )
                )
            }

        }
        return availableInputs
    }

    /**
     * generate outputs
     * @param fBox: the current configured box
     */
    fun generateOutputs(fBox: FBox?): ArrayList<TFInputBoxValue> {
        val availableOutputs = arrayListOf<TFInputBoxValue>()
        when (fBox) {
            is FBoxEventDevice -> {
                if (!fBox.devId.isNullOrEmpty()) {
                    val device = FlowSdk.deviceHandler().get(fBox.devId)
                    device?.let {
//                        device.features.forEach { feature ->
//                            availableOutputs.add(Pair(TFInOutType.PAYLOAD, feature))
//                        }
                    }
                }
            }

            else -> {

            }
        }
        return availableOutputs
    }


    fun createFlowScene(
        flowSceneId: String,
        devId: String,
        flowSceneLabel: String
    ) {
        FlowSdk.flowScenarioHandler().createFlowScenario(
            flowSceneId,
                    devId,
            flowSceneLabel,
            "",
            object : RequestResultCallback<TFFlowScenario> {
                override fun onResult(p0: TFFlowScenario?) {

                }

                override fun onError(p0: Int) {

                }
            }
        )
    }

    fun createSceneBoxes(
        flowSceneId: String,
        devId: String,
        boxes: ArrayList<FBox>
    ) {
        FlowSdk.flowScenarioHandler().bindBoxesScenario(
            flowSceneId,
            devId,
            boxes,
            object : SuccessStatusCallback {
                override fun onSuccess() {

                }

                override fun onFailure(p0: Int, p1: String?) {

                }
            }
        )
    }
}