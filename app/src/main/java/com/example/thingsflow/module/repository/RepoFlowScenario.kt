package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.ui.customview.LayoutZoomPan
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import javax.inject.Inject

class RepoFlowScenario @Inject constructor() {
    private val TAG = "RepoFlowScenario"

    val handler = FlowSdk.flowHandler()

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

    fun generateInputsFromPreviousBox(fBox: FBox?): ArrayList<Pair<TFInOutType, Int>> {
        val availableInputs: ArrayList<Pair<TFInOutType, Int>> = arrayListOf()
        when (fBox) {
            is FBoxEventDevice -> {
                if (fBox.devType != IoTDeviceType.ALL) {
                    availableInputs.add(Pair(TFInOutType.DEVICE_TYPE, fBox.devType))
                }

                if (fBox.attrTypes.isNotEmpty()) {
                    fBox.attrTypes.forEach {
                        availableInputs.add(Pair(TFInOutType.ATTRIBUTE, it))
                    }
                }

                if (!fBox.devId.isNullOrEmpty()) {
                    val device = FlowSdk.deviceHandler().get(fBox.devId)
                    device?.let {
                        it.features.forEach { feature ->
                            availableInputs.add(Pair(TFInOutType.PAYLOAD, feature))
                        }
                    }
                }
            }

            is FBoxActionControlDevice -> {
                if (fBox.devType != IoTDeviceType.ALL) {
                    availableInputs.add(Pair(TFInOutType.DEVICE_TYPE, fBox.devType))
                }
            }
        }
        return availableInputs
    }

    fun generateInputsFromSpecificDevices(
        devType: Int?,
        attrs: IntArray?,
        devMap: HashMap<String?, IntArray>?
    ): ArrayList<Pair<TFInOutType, Int>> {
        val availableInputs: ArrayList<Pair<TFInOutType, Int>> = arrayListOf()
        if (devType != null && devType != IoTDeviceType.ALL) {
            availableInputs.add(Pair(TFInOutType.DEVICE_TYPE, devType))
        }

        attrs?.let {
            if (attrs.isNotEmpty()) {
                attrs.forEach {
                    availableInputs.add(Pair(TFInOutType.ATTRIBUTE, it))
                }
            }
        }


        devMap?.let {
            if (!devMap.isEmpty()) {
                devMap.forEach { deviceEntry ->
                    val device = FlowSdk.deviceHandler().get(deviceEntry.key)
                    device?.let {
                        it.features.forEach { feature ->
                            availableInputs.add(Pair(TFInOutType.PAYLOAD, feature))
                        }
                    }
                }

            }
        }

        return availableInputs
    }

    fun generateOutputs(fBox: FBox?): ArrayList<Pair<TFInOutType, Int>> {
        val availableOutputs = arrayListOf<Pair<TFInOutType, Int>>()
        when(fBox) {
            is FBoxEventDevice -> {
                if (!fBox.devId.isNullOrEmpty()) {
                    val device = FlowSdk.deviceHandler().get(fBox.devId)
                    device?.let {
                        device.features.forEach { feature->
                            availableOutputs.add(Pair(TFInOutType.PAYLOAD, feature))
                        }
                    }
                }
            }
            else -> {

            }
        }
        return availableOutputs
    }

}