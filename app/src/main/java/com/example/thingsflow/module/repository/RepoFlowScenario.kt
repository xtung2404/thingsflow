package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.ui.customview.LayoutZoomPan
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
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
                    availableInputs.addAll(generateInputsFromDevice(fBox.devId))
                }
            }

            is FBoxActionControlDevice -> {

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
                    availableInputs.addAll(generateInputsFromDevice(deviceEntry.key))
                }

            }
        }

        return availableInputs
    }

    /**
     * generate state inputs basing on information of a device
     */
    private fun generateInputsFromDevice(devId: String?): ArrayList<TFInputBoxValue> {
        val availableInputs = arrayListOf<TFInputBoxValue>()
        val device = FlowSdk.deviceHandler().get(devId)
        device?.let {
            when (device.elementIds.size) {
                1 -> {
                    device.features.forEach { feature ->
                        availableInputs.add(
                            TFInputBoxValue(
                                device.uuid,
                                device.elementIds[0],
                                TFInOutType.PAYLOAD_STATE,
                                feature
                            )
                        )
                    }
                }

                else -> {
                    device.elementInfos.forEach { elmInfo ->
                        if (elmInfo.value.attrInfos != null && elmInfo.value.attrInfos.isNotEmpty()) {
                            elmInfo.value.attrInfos.forEach { feature ->
                                availableInputs.add(
                                    TFInputBoxValue(
                                        device.uuid,
                                        elmInfo.key,
                                        TFInOutType.PAYLOAD_STATE,
                                        feature
                                    )
                                )
                            }
                        } else {
                            device.features.forEach { feature ->
                                availableInputs.add(
                                    TFInputBoxValue(
                                        device.uuid,
                                        elmInfo.key,
                                        TFInOutType.PAYLOAD_STATE,
                                        feature
                                    )
                                )
                            }
                        }
                    }
                }
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

}