package com.example.thingsflow.ui.flowbinding

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowBindingBinding
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterSpinnerGatewayBinding
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxEventFromDevice
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.base.ILogR
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGPT
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGemini
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionCodeFunction
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDLearn
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRecognize
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRemove
import rogo.iot.module.flowcommon.box.action.FBoxActionHandlerAnotherBox
import rogo.iot.module.flowcommon.box.action.FBoxActionPublishMqtt
import rogo.iot.module.flowcommon.box.action.FBoxActionSendWebSocket
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionTime
import rogo.iot.module.flowcommon.box.event.FBoxEventCameraStreaming
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventFaceID
import rogo.iot.module.flowcommon.box.event.FBoxEventHttpServerApi
import rogo.iot.module.flowcommon.box.event.FBoxEventIORS232
import rogo.iot.module.flowcommon.box.event.FBoxEventIORS485
import rogo.iot.module.flowcommon.box.event.FBoxEventMqtt
import rogo.iot.module.flowcommon.box.event.FBoxEventSchedule
import rogo.iot.module.flowcommon.box.event.FBoxEventStatistic
import rogo.iot.module.flowcommon.box.event.FBoxEventTimerInterval
import rogo.iot.module.flowcommon.box.event.FBoxEventTouchID
import rogo.iot.module.flowcommon.box.event.FBoxEventVoiceRecognize
import rogo.iot.module.flowcommon.box.event.FBoxEventWeather
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback

@AndroidEntryPoint
class FragmentFlowBinding : FragmentBase<FragmentFlowBindingBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_binding

    private val TAG = "FragmentFlowBinding"
    private val vmFlowBinding: VMFlowBinding by activityViewModels<VMFlowBinding>()
    var boxes = arrayListOf<FBox?>()

    private val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext(),
        )
    }

    private lateinit var adapterSpinnerGatewayBinding: AdapterSpinnerGatewayBinding
    private lateinit var overlayBindingBoxEventFromDevice: OverlayBindingBoxEventFromDevice


    override fun initVariable() {
        super.initVariable()
        binding.apply {
            binding.boxLayout.onBoxClickListener = this@FragmentFlowBinding

        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = "Danh sách các thiết bị đang triển khai"
            adapterSpinnerGatewayBinding = AdapterSpinnerGatewayBinding(requireContext(), vmFlowBinding.getSelectedGateways().entries.toList())
            spinnerGateway.adapter = adapterSpinnerGatewayBinding

            vmFlowBinding.boxes.observe(this@FragmentFlowBinding) {
                boxLayout.boxList = ArrayList(it)
            }


        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentFlowScenarioInfo)
            }
            btnUpdateLabel.setOnClickListener {
                dialogLabelFlowScenario.show()
            }


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

    override fun onBoxClick(box: FBox?) {
        ILogR.D(TAG, "onBoxClick", box?.id)
        when (box) {
            is FBoxEventDevice -> {
                overlayBindingBoxEventFromDevice.show(box)
            }

            is FBoxEventMqtt,
            is FBoxEventWeather,
            is FBoxEventSchedule,
            is FBoxEventCameraStreaming,
            is FBoxEventFaceID,
            is FBoxEventHttpServerApi,
            is FBoxEventIORS232,
            is FBoxEventIORS485,
            is FBoxEventTouchID,
            is FBoxEventVoiceRecognize,
            is FBoxEventTimerInterval,
            is FBoxEventStatistic
                -> {

            }

            is FBoxActionConditionGeneral,
            is FBoxActionConditionTime,
            is FBoxActionConditionDeviceState
                -> {

            }

            is FBoxActionAIGPT,
            is FBoxActionAIGemini,
            is FBoxActionCallHttp,
            is FBoxActionControlDevice,
            is FBoxActionCodeFunction,
            is FBoxActionFaceIDLearn,
            is FBoxActionFaceIDRecognize,
            is FBoxActionFaceIDRemove,
            is FBoxActionHandlerAnotherBox,
            is FBoxActionPublishMqtt,
            is FBoxActionSendWebSocket
                -> {

            }

            else -> {

            }
        }
    }
}