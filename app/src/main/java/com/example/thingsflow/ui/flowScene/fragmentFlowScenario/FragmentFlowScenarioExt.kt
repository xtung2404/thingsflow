package com.example.thingsflow.ui.flowScene.fragmentFlowScenario

import android.view.View
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionCallHttp
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionConditionGeneral
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionControlDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxEventFromDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigInputBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxActionType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxConditionType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxEventType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlaySetControlDevice
import com.example.thingsflow.utils.FTypeBox
import rogo.iot.module.flowcommon.type.FTypeAction
import rogo.iot.module.flowcommon.type.FTypeEvent

//handle overlays select type of box
fun FragmentFlowScenario.handleOverlaySelectTypeBox() {
    handleOverlaySelectBoxEventType()
    handleOverlaySelectBoxType()
    handleOverlaySelectBoxActionType()
    handleOverlaySelectBoxActionConditionType()
}

// handler overlays config box-events
fun FragmentFlowScenario.handleOverlayConfigBoxEvent() {
    handleOverlayConfigBoxEventFromDevice()
}

// handler overlays config box-actions
fun FragmentFlowScenario.handleOverlayConfigBoxAction() {
    handleOverlayConfigBoxActionCallHttp()
    handleOverlayConfigBoxActionControlDevice()
}

// handler overlays config box-conditions
fun FragmentFlowScenario.handleOverlayConfigBoxActionCondition() {
    handleOverlayConfigBoxActionConditionGeneral()
    handleOverlayConfigBoxActionConditionDeviceState()
}

fun FragmentFlowScenario.handleOverlaySelectDevice() {
    overlaySelectDevice = OverlaySelectDevice(
        requireActivity(),
        binding.overlayContainer,
        onDevicesSelected = { devType, attrs, devMap ->
            if (devMap.isNotEmpty()) {
                overlaySelectDevice.hide()
                when (currentBoxType) {
                    FTypeAction.ACT_CONTROL_DEVICE -> {
                        overlaySetControlDevice.show(attrs, devMap)
                    }

                    FTypeAction.ACT_CONDITION_DEVICE -> {
                        overlayConfigInputBoxActionConditionDeviceState.show(
                            devType,
                            attrs,
                            devMap
                        )
                    }
                }
            }
        },
        onClose = {
            overlaySelectDevice.hide()
        }
    )
}

/**
 * handle when user select type of box event
 * - onBoxEventTypeSelected: triggered when type of box event is selected
 */
fun FragmentFlowScenario.handleOverlaySelectBoxEventType() {
    overlaySelectBoxEventType = OverlaySelectBoxEventType(
        requireContext(),
        binding.overlayContainer,
        onBoxEventTypeSelected = { type ->
            overlaySelectBoxEventType.hide()
            binding.btnEditScene.visibility = View.VISIBLE
            when (type) {
                FTypeEvent.EVT_FROM_DEVICE -> {
                    overlayConfigBoxEventFromDevice.show()
                }

                else -> {
                    overlayConfigBoxEventFromDevice.hide()
                }
            }
        },
        onClose = {
            overlaySelectBoxEventType.hide()
        }
    )
}

/**
 * handle when user select type of box (action or condtion)
 * - onBoxTypeSelected: triggered when type of box is selected
 */
fun FragmentFlowScenario.handleOverlaySelectBoxType() {
    overlaySelectBoxType = OverlaySelectBoxType(
        requireActivity(),
        binding.overlayContainer,
        onBoxTypeSelected = { type ->
            overlaySelectBoxType.hide()
            when (type) {
                FTypeBox.TYPE_BOX_ACTION -> {
                    overlaySelectBoxActionType.show()
                }

                FTypeBox.TYPE_BOX_CONDITION -> {
                    overlaySelectBoxConditionType.show()
                }
            }
        },
        onClose = {
            overlaySelectBoxType.hide()
        }
    )
}

/**
 * handle when user select type of box action(call http, control device, etc...)
 * - onBoxActionTypeSelected: triggered when type of box action is selected
 */
fun FragmentFlowScenario.handleOverlaySelectBoxActionType() {
    overlaySelectBoxActionType = OverlaySelectBoxActionType(
        requireActivity(),
        binding.overlayContainer,
        onBoxActionTypeSelected = { type ->
            overlaySelectBoxActionType.hide()
            currentBoxType = type
            when (type) {
                FTypeAction.ACT_CALL_HTTP -> {
                    overlayConfigBoxActionCallHttp.show()
                }

                FTypeAction.ACT_CONTROL_DEVICE -> {
                    overlayConfigBoxActionControlDevice.show()
                }
            }
        },
        onClose = {
            overlaySelectBoxActionType.hide()
            overlaySelectBoxType.show()
        }
    )
}

/**
 * handle when user select type of box action condition(condition general, device state, etc...)
 * - onBoxConditionTypeSelected: triggered when type of box action condition is selected
 */
fun FragmentFlowScenario.handleOverlaySelectBoxActionConditionType() {
    overlaySelectBoxConditionType = OverlaySelectBoxConditionType(
        requireActivity(),
        binding.overlayContainer,
        onBoxConditionTypeSelected = { type ->
            currentBoxType = type
            overlaySelectBoxConditionType.hide()
            when (type) {
                FTypeAction.ACT_CONDITION_GENERAL -> {
                    overlayConfigBoxActionConditionGeneral.show()
                }

                FTypeAction.ACT_CONDITION_DEVICE -> {
                    overlayConfigBoxActionConditionDeviceState.show()
                }
            }
        },
        onClose = {
            overlaySelectBoxConditionType.hide()
        }
    )
}

/**
 * handle when user create a new box event from device
 * - onBoxEventCreated: triggered when a new box event is created
 */
private fun FragmentFlowScenario.handleOverlayConfigBoxEventFromDevice() {
    overlayConfigBoxEventFromDevice = OverlayConfigBoxEventFromDevice(
        requireActivity(),
        binding.overlayContainer,
        onBoxEventCreated = {
            overlayConfigBoxEventFromDevice.hide()
            vmFlowScenario.boxes.value?.clear()
            vmFlowScenario.configBox(it, null)
        },
        onClose = { isBackable ->
            overlayConfigBoxEventFromDevice.hide()
            if (isBackable) {
                overlaySelectBoxEventType.show()
            }
        }
    )
}

/**
 * handle when user create a new box action call http
 * - onConfigHeader: triggered when user config HTTP headers
 * - onBoxActionCallHttpCreated: triggered when a new box event is created
 */
private fun FragmentFlowScenario.handleOverlayConfigBoxActionCallHttp() {
    overlayConfigBoxActionCallHttp = OverlayConfigBoxActionCallHttp(
        requireActivity(),
        binding.overlayContainer,
        onConfigHeader = {
            overlayConfigBoxActionCallHttp.hide()
            dialogConfigHeaderHttp.show()
        },
        onBoxActionCallHttpCreated = {
            overlayConfigBoxActionCallHttp.hide()
            vmFlowScenario.configBox(it, newSegType)
        },
        onClose = {
            overlayConfigBoxActionCallHttp.hide()
        }
    )
}

/**
 * handle when user create a new box action call http
 * - onConfigHeader: triggered when user config HTTP headers
 * - onBoxActionCallHttpCreated: triggered when a new box event is created
 */
private fun FragmentFlowScenario.handleOverlayConfigBoxActionControlDevice() {
    overlayConfigBoxActionControlDevice = OverlayConfigBoxActionControlDevice(
        requireActivity(),
        binding.overlayContainer,
        onSelectDevice = { devType, attrs ->
            overlayConfigBoxActionControlDevice.hide()
            overlaySelectDevice.show(devType, attrs)
        },
        onBoxActionControlDeviceCreated = {
            overlayConfigBoxActionControlDevice.hide()
            vmFlowScenario.configBox(it, newSegType)
        },
        onClose = {
            overlayConfigBoxActionControlDevice.hide()
        }
    )

    overlaySetControlDevice = OverlaySetControlDevice(
        requireActivity(),
        binding.overlayContainer,
        onCommandSetted = {

        },
        onClose = {
            overlaySetControlDevice.hide()
            overlaySelectDevice.show()
        }

    )
}

/**
 * handle when user create a new box action condition general
 * - onBoxActionCondtionGeneralCreated: triggered when a new box action condition general is created
 */
private fun FragmentFlowScenario.handleOverlayConfigBoxActionConditionGeneral() {
    overlayConfigBoxActionConditionGeneral = OverlayConfigBoxActionConditionGeneral(
        requireActivity(),
        binding.overlayContainer,
        onBoxActionCondtionGeneralCreated = {
            overlayConfigBoxActionConditionGeneral.hide()
            vmFlowScenario.configBox(it, newSegType)
        },
        onClose = { isBackable ->
            overlayConfigBoxActionConditionGeneral.hide()
            if (isBackable) {
                overlaySelectBoxConditionType.show()
            }
        }
    )
}

/**
 * handle when user create a new box action condition general
 */
private fun FragmentFlowScenario.handleOverlayConfigBoxActionConditionDeviceState() {
    overlayConfigBoxActionConditionDeviceState = OverlayConfigBoxActionConditionDeviceState(
        requireActivity(),
        binding.overlayContainer,
        // triggered when user config inputs for box
        onConfigInput = {
            overlayConfigInputBoxActionConditionDeviceState.show()
        },
        // triggered when a new box action condition device state is created
        onBoxActionCondtionDeviceStateCreated = {
            overlayConfigBoxActionConditionDeviceState.hide()
            vmFlowScenario.configBox(it, newSegType)
        },
        onClose = {
            overlayConfigBoxActionConditionDeviceState.hide()
            overlaySelectBoxConditionType.show()
        }
    )

    /**
     * handle when user config input for box
     */
    overlayConfigInputBoxActionConditionDeviceState =
        OverlayConfigInputBoxActionConditionDeviceState(
            requireActivity(),
            binding.overlayContainer,
            // triggered when user select a specific device as a input(based on device type and attributes)
            onSelectDevice = { devType, attrs ->
                overlaySelectDevice.show(devType, attrs)
            },
            // triggered when the new input is setted
            onInputSetted = { devType, attrs, devMap ->
                overlayConfigInputBoxActionConditionDeviceState.hide()
                overlayConfigBoxActionConditionDeviceState.show(devType, attrs, devMap)
            },
            onClose = {
                overlayConfigInputBoxActionConditionDeviceState.hide()
            }
        )
}
