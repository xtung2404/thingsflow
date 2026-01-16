package com.example.thingsflow.ui.flowbinding

import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigInputBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigOutputJson
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlaySetControlDevice
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionCallHttp
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionConditionGeneral
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionControlDevice
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxEventFromDevice
import rogo.iot.module.flowcommon.type.FBoxType


fun FragmentFlowBinding.handleBindingBoxes() {
    handleBindingBoxesEvent()
    handleBindingBoxesAction()
    handleBindingBoxesActionCondition()
}

fun FragmentFlowBinding.handleBindingBoxesEvent() {
    handleBindingBoxEventDevice()
}

fun FragmentFlowBinding.handleBindingBoxesAction() {
    handleBindingBoxActionControlDevice()
    handleBindingBoxActionCallHttp()
}

fun FragmentFlowBinding.handleBindingBoxesActionCondition() {
    handleBindingBoxActionConditionDeviceState()
    handleBindingBoxActionConditionGeneral()
}

fun FragmentFlowBinding.handleBindingBoxEventDevice() {
    binding.apply {
        overlayBindingBoxEventFromDevice = OverlayBindingBoxEventFromDevice(
            requireActivity(),
            binding.overlayBindingContainer,
            onSelectDevice = { devType, attrs, selectedDevices->
                overlayBindingBoxEventFromDevice.hide()
                overlaySelectDevice.show(FBoxType.EVT_FROM_DEVICE, devType, attrs, selectedDevices)
            },
            onSave = { fBox ->
                overlayBindingBoxEventFromDevice.hide()
                vmFlowBinding.updateBoxInfo(fBox)
            },
            onClose = {
                overlayBindingBoxEventFromDevice.hide()
            }
        )
    }
}

fun FragmentFlowBinding.handleBindingBoxActionControlDevice() {
    binding.apply {
        overlayBindingBoxActionControlDevice = OverlayBindingBoxActionControlDevice(
            requireActivity(),
            binding.overlayBindingContainer,
            onSelectDevice = {devType, attrs, devMap ->
                overlayBindingBoxActionControlDevice.hide()
                overlaySelectDevice.show(currentBoxType, devType, attrs, devMap)
            },
            onSave = { fBoxActionControlDevice ->
                overlayBindingBoxActionControlDevice.hide()
                vmFlowBinding.updateBoxInfo(fBoxActionControlDevice)
            },
            onClose = {
                overlayBindingBoxActionControlDevice.hide()
            }
        )
    }
}

fun FragmentFlowBinding.handleOverlaySelectDevice() {
    binding.apply {
        overlaySelectDevice = OverlaySelectDevice(
            requireActivity(),
            overlayBindingContainer,
            onDevicesSelected = { devType, attrs, devMap ->
                overlaySelectDevice.hide()
                when(currentBoxType) {
                    FBoxType.EVT_FROM_DEVICE -> {
                        overlayBindingBoxEventFromDevice.show(devType, attrs, devMap)
                    }
                    FBoxType.ACT_CONTROL_DEVICE -> {
                        overlaySetControlDevice.show(devType, attrs, devMap)
                    }
                    FBoxType.ACT_CONDITION_DEVICE -> {
                        overlayBindingBoxActionConditionDeviceState.show(devType, attrs, devMap)
                    }
                }
            },
            onClose = {
                overlaySelectDevice.hide()
            }
        )
    }
}

fun FragmentFlowBinding.handleOverlaySetControlDevice() {
    binding.apply {
        overlaySetControlDevice = OverlaySetControlDevice(
            requireActivity(),
            overlayBindingContainer,
            onCommandSetted = { devType, attrs, devActionMap ->
                overlaySetControlDevice.hide()
                overlayBindingBoxActionControlDevice.show(devType, attrs, devActionMap)
            },
            onClose = {
                overlaySetControlDevice.hide()
            }
        )
    }
}

fun FragmentFlowBinding.handleBindingBoxActionCallHttp() {
    binding.apply {
        overlayBindingBoxActionCallHttp = OverlayBindingBoxActionCallHttp(
            requireActivity(),
            overlayBindingContainer,
            onConfigHeader = {
                overlayBindingBoxActionCallHttp.hide()
                dialogConfigHeaderHttp.show()
            },
            onConfigJsonOutput = {
                overlayBindingBoxActionCallHttp.hide()
                overlayConfigOutputJson.show()
            },
            onBoxActionCallHttpCreated = { fBoxActionCallHttp->
                overlayBindingBoxActionCallHttp.hide()
                vmFlowBinding.updateBoxInfo(fBoxActionCallHttp)
            },
            onClose = {
                overlayBindingBoxActionCallHttp.hide()
            }
        )
        overlayConfigOutputJson = OverlayConfigOutputJson(
            requireActivity(),
            overlayBindingContainer,
            onOutputConfigured = { fieldList ->
                overlayConfigOutputJson.hide()
                overlayBindingBoxActionCallHttp.show(fieldList)
            },
            onClose = {
                overlayConfigOutputJson.hide()
            }
        )
    }
}

fun FragmentFlowBinding.handleBindingBoxActionConditionDeviceState() {
    binding.apply {
        overlayBindingBoxActionConditionDeviceState = OverlayBindingBoxActionConditionDeviceState(
            requireActivity(),
            overlayBindingContainer,
            onConfigInput = { devType, attrs, devMap ->
                overlayBindingBoxActionConditionDeviceState.hide()
                overlayConfigInputBoxActionConditionDeviceState.show()
            },
            onBoxActionCondtionDeviceStateUpdated = { fBoxActionConditionDeviceState ->
                overlayBindingBoxActionConditionDeviceState.hide()
                vmFlowBinding.updateBoxInfo(fBoxActionConditionDeviceState)
            },
            onClose = {
                overlayBindingBoxActionConditionDeviceState.hide()
            }
        )

        /**
         * handle when user config input for box
         */
        overlayConfigInputBoxActionConditionDeviceState =
            OverlayConfigInputBoxActionConditionDeviceState(
                requireActivity(),
                overlayBindingContainer,
                // triggered when user select a specific device as a input(based on device type and attributes)
                onSelectDevice = { devType, attrs ->
                    overlayConfigInputBoxActionConditionDeviceState.hide()
                    overlaySelectDevice.show(currentBoxType, devType, attrs)
                },
                // triggered when the new input is setted
                onInputSetted = { devType, attrs, devMap ->
                    overlayConfigInputBoxActionConditionDeviceState.hide()
                    overlayBindingBoxActionConditionDeviceState.show(devType, attrs, devMap)
                },
                onClose = {
                    overlayConfigInputBoxActionConditionDeviceState.hide()
                    overlayBindingBoxActionConditionDeviceState.show()
                }
            )
    }
}

fun FragmentFlowBinding.handleBindingBoxActionConditionGeneral() {
    binding.apply {
        overlayBindingBoxActionConditionGeneral = OverlayBindingBoxActionConditionGeneral(
            requireActivity(),
            overlayBindingContainer,
            onBoxActionCondtionGeneralUpdated = {fBoxActionConditionGeneral->
                overlayBindingBoxActionConditionGeneral.hide()
                vmFlowBinding.updateBoxInfo(fBoxActionConditionGeneral)
            },
            onClose = {
                overlayBindingBoxActionConditionGeneral.hide()
            }
        )
    }
}
