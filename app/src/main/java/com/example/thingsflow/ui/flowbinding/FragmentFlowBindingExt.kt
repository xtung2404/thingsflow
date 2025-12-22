package com.example.thingsflow.ui.flowbinding

import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectDevice
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
}

fun FragmentFlowBinding.handleBindingBoxesActionCondition() {

}

fun FragmentFlowBinding.handleBindingBoxEventDevice() {
    binding.apply {
        overlayBindingBoxEventFromDevice = OverlayBindingBoxEventFromDevice(
            requireActivity(),
            binding.overlayBindingContainer,
            onSave = { fBox ->
                overlayBindingBoxEventFromDevice.hide()
                vmFlowBinding.updateBox(fBox)
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
            onSave = {
                overlayBindingBoxActionControlDevice.hide()
            },
            onClose = {
                overlayBindingBoxActionControlDevice.hide()
            }
        )
    }
}

fun FragmentFlowBinding.handlerOverlaySelectDevice() {
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
                        overlayBindingBoxActionControlDevice.show()
                    }
                }
            },
            onClose = {
                overlaySelectDevice.hide()
            }
        )
    }
}