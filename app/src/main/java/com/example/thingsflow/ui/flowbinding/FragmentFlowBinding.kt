package com.example.thingsflow.ui.flowbinding

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowBindingBinding
import com.example.thingsflow.module.define.TFHttpHeader
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.module.viewmodel.VMFlowScene
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterSpinnerGatewayBinding
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogConfigHeaderHttp
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigInputBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigOutputJson
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlaySetControlDevice
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionCallHttp
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionConditionGeneral
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxActionControlDevice
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxEventFromDevice
import dagger.hilt.android.AndroidEntryPoint
import rgsm.cu
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
import rogo.iot.module.flowcommon.type.FBoxType
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback

@AndroidEntryPoint
class FragmentFlowBinding : FragmentBase<FragmentFlowBindingBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_binding

    private val TAG = "FragmentFlowBinding"
    internal val vmFlowBinding: VMFlowBinding by activityViewModels<VMFlowBinding>()

    internal var currentBoxType: Int = -1

    internal val dialogConfigHeaderHttp: DialogConfigHeaderHttp by lazy {
        DialogConfigHeaderHttp(
            requireActivity(),
            onHeadersConfiged = { headers ->
                dialogConfigHeaderHttp.dismiss()
                overlayBindingBoxActionCallHttp.show(headers)
            },
            onClose = {
                dialogConfigHeaderHttp.dismiss()
                overlayBindingBoxActionCallHttp.show(arrayListOf<TFHttpHeader>())
            }
        )
    }
    private val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext(),
            onLabelChanged = { label->

            }
        )
    }

    private lateinit var adapterSpinnerGatewayBinding: AdapterSpinnerGatewayBinding
    internal lateinit var overlayBindingBoxEventFromDevice: OverlayBindingBoxEventFromDevice
    internal lateinit var overlayBindingBoxActionControlDevice: OverlayBindingBoxActionControlDevice
    internal lateinit var overlaySetControlDevice: OverlaySetControlDevice
    internal lateinit var overlayBindingBoxActionCallHttp: OverlayBindingBoxActionCallHttp
    internal lateinit var overlayBindingBoxActionConditionDeviceState: OverlayBindingBoxActionConditionDeviceState
    internal lateinit var overlayConfigInputBoxActionConditionDeviceState: OverlayConfigInputBoxActionConditionDeviceState //use when create an input from a device for box action condition device state

    internal lateinit var overlayBindingBoxActionConditionGeneral: OverlayBindingBoxActionConditionGeneral
    internal lateinit var overlaySelectDevice: OverlaySelectDevice
    internal lateinit var overlayConfigOutputJson: OverlayConfigOutputJson



    override fun initVariable() {
        super.initVariable()
        binding.apply {
            binding.boxLayout.onBoxClickListener = this@FragmentFlowBinding

        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            handleBindingBoxes()
            handleOverlaySelectDevice()
            handleOverlaySetControlDevice()
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

            btnSave.setOnClickListener {
                val selectedGateway = spinnerGateway.selectedItem as Map.Entry<String?, IntArray>
                vmFlowBinding.createFlowBinding(
                    selectedGateway.key!!,
                    "",
                    "",
                    object : SuccessStatusCallback {
                        override fun onSuccess() {
                            ILogR.D(TAG, "onBindingSuccess")
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            ILogR.D(TAG, "onBindingFalure", p0, p1)
                        }

                    }
                )

                vmFlowBinding.bindBoxes(
                    selectedGateway.key!!,
                    "bindingId",
                    vmFlowBinding.boxes.value as ArrayList<FBox?>,
                    object : SuccessStatusCallback {
                        override fun onSuccess() {
                            ILogR.D(TAG, "onBindBoxesSuccess")
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            ILogR.D(TAG, "onBindBoxesFailure", p0, p1)
                        }

                    }
                )
            }
        }
    }

    override fun onBoxClick(box: FBox?) {
        ILogR.D(TAG, "onBoxClick", box?.id)
        vmFlowBinding.setSelectedBox(box)
        when (box) {
            is FBoxEventDevice -> {
                currentBoxType = FBoxType.EVT_FROM_DEVICE
                overlayBindingBoxEventFromDevice.show()
            }

            is FBoxActionControlDevice -> {
                currentBoxType = FBoxType.ACT_CONTROL_DEVICE
                overlayBindingBoxActionControlDevice.show()
            }

            is FBoxActionCallHttp -> {
                currentBoxType = FBoxType.ACT_CALL_HTTP
                overlayBindingBoxActionCallHttp.show()
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

            is FBoxActionConditionGeneral -> {
                currentBoxType = FBoxType.ACT_CONDITION_GENERAL
                overlayBindingBoxActionConditionGeneral.show()
            }

            is FBoxActionConditionTime -> {

            }
            is FBoxActionConditionDeviceState -> {
                currentBoxType = FBoxType.ACT_CONDITION_DEVICE
                overlayBindingBoxActionConditionDeviceState.show()
            }

            is FBoxActionAIGPT,
            is FBoxActionAIGemini,
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