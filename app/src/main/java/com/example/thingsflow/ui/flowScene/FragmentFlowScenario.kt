package com.example.thingsflow.ui.flowScene

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxEventFromDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxActionType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxConditionType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxEventType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxType
import com.example.thingsflow.utils.FTypeBox
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGPT
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGemini
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionCodeFunction
import rogo.iot.module.flowcommon.box.action.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.action.FBoxActionConditionTime
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDLearn
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRecognize
import rogo.iot.module.flowcommon.box.action.FBoxActionFaceIDRemove
import rogo.iot.module.flowcommon.box.action.FBoxActionHandlerAnotherBox
import rogo.iot.module.flowcommon.box.action.FBoxActionPublishMqtt
import rogo.iot.module.flowcommon.box.action.FBoxActionSendWebSocket
import rogo.iot.module.flowcommon.box.event.FBoxEvent
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
import rogo.iot.module.flowcommon.type.FTypeEvent
import rogo.iot.module.platform.ILogR

@AndroidEntryPoint
class FragmentFlowScenario : FragmentBase<FragmentFlowScenarioBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario

    private val TAG = "FragmentFlowScenario"
    private lateinit var overlaySelectBoxEventType: OverlaySelectBoxEventType
    private lateinit var overlayConfigBoxEventFromDevice: OverlayConfigBoxEventFromDevice
    private lateinit var overlaySelectBoxType: OverlaySelectBoxType
    private lateinit var overlaySelectBoxActionType: OverlaySelectBoxActionType
    private lateinit var overlaySelectBoxConditionType: OverlaySelectBoxConditionType

    val boxes = mutableListOf<FBox>()
    private val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext()
        )
    }

    override fun initVariable() {
        super.initVariable()
        binding.apply {

        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = context?.getString(R.string.list_of_flow_scenario)
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentFlowScenManagement)
            }
            // Khởi tạo một danh sách FBox mới
            boxes.clear()
            if (boxes.isEmpty()) {
                btnEditScene.visibility = View.GONE
                boxLayout.isEditMode = true
            } else {
                btnEditScene.visibility = View.VISIBLE
                boxLayout.isEditMode = false
            }
            val fBoxEvent = FBoxEventDevice()
//            fBoxEvent.devId = "123"
//            fBoxEvent.devType = IoTDeviceType.SWITCH
//            fBoxEvent.targetSegId = 1
            boxes.add(fBoxEvent)
//             Gán danh sách cho boxList của LayoutZoomPan
            boxLayout.boxList = ArrayList(boxes)
            binding.boxLayout.onBoxClickListener = this@FragmentFlowScenario
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnUpdateLabel.setOnClickListener {
                dialogLabelFlowScenario.show()
            }

            btnEditScene.setOnClickListener {
                boxLayout.isEditMode = !boxLayout.isEditMode
            }

            boxLayout.onBoxActionListener = object : LayoutZoomPan.OnBoxActionListener {
                override fun onAddBoxClicked(box: FBox) {
                    when(box) {
                        is FBoxEvent -> {
                            overlaySelectBoxType.show()
                        }
                    }
                }

                override fun onRemoveBoxClicked(box: FBox) {

                }
            }


            overlaySelectBoxEventType = OverlaySelectBoxEventType(
                requireContext(),
                binding.overlayContainer,
                onBoxEventTypeSelected = {
                    overlaySelectBoxEventType.hide()
                    when(it) {
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

            overlayConfigBoxEventFromDevice = OverlayConfigBoxEventFromDevice(
                requireActivity(),
                binding.overlayContainer,
                onBoxEventCreated = {
                    overlayConfigBoxEventFromDevice.hide()
                    boxes.clear()
                    boxes.add(it)
                    boxLayout.boxList = ArrayList(boxes)
                },
                onClose = {
                    overlayConfigBoxEventFromDevice.hide()
                }
            )

            overlaySelectBoxType = OverlaySelectBoxType(
                requireActivity(),
                binding.overlayContainer,
                onBoxTypeSelected = {
                    when(it) {
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

            overlaySelectBoxActionType = OverlaySelectBoxActionType(
                requireActivity(),
                binding.overlayContainer,
                onBoxActionTypeSelected = {

                },
                onClose = {
                    overlaySelectBoxActionType.hide()
                }
            )

            overlaySelectBoxConditionType = OverlaySelectBoxConditionType(
                requireActivity(),
                binding.overlayContainer,
                onBoxConditionTypeSelected = {

                },
                onClose = {
                    overlaySelectBoxConditionType.hide()
                }
            )
        }
    }

    override fun onBoxClick(box: FBox?) {
        ILogR.D(TAG, "onBoxClick")
        when (box) {
            is FBoxEventDevice,
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
                overlaySelectBoxEventType.show()
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