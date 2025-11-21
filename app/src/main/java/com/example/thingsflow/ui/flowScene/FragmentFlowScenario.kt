package com.example.thingsflow.ui.flowScene

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionCallHttp
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxActionConditionGeneral
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigBoxEventFromDevice
import com.example.thingsflow.ui.flowScene.overlay.OverlayConfigInputBoxActionConditionDeviceState
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxActionType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxConditionType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxEventType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectBoxType
import com.example.thingsflow.ui.flowScene.overlay.OverlaySelectDevice
import com.example.thingsflow.utils.FTypeBox
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGPT
import rogo.iot.module.flowcommon.box.action.FBoxActionAIGemini
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionCodeFunction
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionTime
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
import rogo.iot.module.flowcommon.type.FTypeAction
import rogo.iot.module.flowcommon.type.FTypeEvent
import rogo.iot.module.platform.ILogR

@AndroidEntryPoint
class FragmentFlowScenario : FragmentBase<FragmentFlowScenarioBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario

    private val TAG = "FragmentFlowScenario"

    //Màn hình lựa chọn loại sự kiện
    private lateinit var overlaySelectBoxEventType: OverlaySelectBoxEventType
    private lateinit var overlayConfigBoxEventFromDevice: OverlayConfigBoxEventFromDevice
    private lateinit var overlaySelectBoxType: OverlaySelectBoxType
    private lateinit var overlaySelectBoxActionType: OverlaySelectBoxActionType
    private lateinit var overlaySelectBoxConditionType: OverlaySelectBoxConditionType
    private lateinit var overlayConfigBoxActionConditionGeneral: OverlayConfigBoxActionConditionGeneral
    private lateinit var overlayConfigBoxActionConditionDeviceState: OverlayConfigBoxActionConditionDeviceState
    private lateinit var overlayConfigBoxActionCallHttp: OverlayConfigBoxActionCallHttp
    private lateinit var overlayConfigInputBoxActionConditionDeviceState: OverlayConfigInputBoxActionConditionDeviceState
    private lateinit var overlaySelectDevice: OverlaySelectDevice
    private var rootBoxId: String? = null
    private var addBoxType: LayoutZoomPan.OnBoxActionListener.AddType? = null

    val boxes = mutableListOf<FBox>()
    private val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext()
        )
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
                btnEditScene.setImageDrawable(context?.getDrawable(R.drawable.ic_check))
            } else {
                btnEditScene.visibility = View.VISIBLE
                boxLayout.isEditMode = false
                btnEditScene.setImageDrawable(context?.getDrawable(R.drawable.ic_edit))
            }
            val fBoxEvent = FBoxEventDevice()
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
                if (boxLayout.isEditMode) {
                    btnEditScene.setImageDrawable(context?.getDrawable(R.drawable.ic_check))
                } else {
                    btnEditScene.setImageDrawable(context?.getDrawable(R.drawable.ic_edit))
                }
            }

            boxLayout.onBoxActionListener = object : LayoutZoomPan.OnBoxActionListener {
                override fun onAddBoxPositiveClicked(
                    box: FBox,
                    addType: LayoutZoomPan.OnBoxActionListener.AddType
                ) {
                    ILogR.D(TAG, "onAddBoxClicked")
                    addBoxType = addType
                    when (box) {
                        is FBoxEvent -> {
                            overlaySelectBoxType.show()
                        }

                        is FBoxAction -> {
                            rootBoxId = box.id
                            overlaySelectBoxType.show()
                        }
                    }
                }

                override fun onRemoveBoxClicked(box: FBox) {
                    ILogR.D(TAG, "onRemoveBoxClicked")
                }
            }


            overlaySelectBoxEventType = OverlaySelectBoxEventType(
                requireContext(),
                binding.overlayContainer,
                onBoxEventTypeSelected = {
                    overlaySelectBoxEventType.hide()
                    btnEditScene.visibility = View.VISIBLE
                    when (it) {
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
                    configBox(it)
                },
                onClose = { isBackable ->
                    overlayConfigBoxEventFromDevice.hide()
                    if (isBackable) {
                        overlaySelectBoxEventType.show()
                    }
                }
            )

            overlaySelectBoxType = OverlaySelectBoxType(
                requireActivity(),
                binding.overlayContainer,
                onBoxTypeSelected = {
                    overlaySelectBoxType.hide()
                    when (it) {
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
                    overlaySelectBoxActionType.hide()
                    when (it) {
                        FTypeAction.ACT_CALL_HTTP -> {
                            overlayConfigBoxActionCallHttp.show()
                        }
                    }
                },
                onClose = {
                    overlaySelectBoxActionType.hide()
                    overlaySelectBoxType.show()
                }
            )

            overlaySelectBoxConditionType = OverlaySelectBoxConditionType(
                requireActivity(),
                binding.overlayContainer,
                onBoxConditionTypeSelected = {
                    overlaySelectBoxConditionType.hide()
                    when (it) {
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


            overlayConfigBoxActionConditionGeneral = OverlayConfigBoxActionConditionGeneral(
                requireActivity(),
                binding.overlayContainer,
                onBoxActionCondtionGeneralCreated = {
                    overlayConfigBoxActionConditionGeneral.hide()
                    configBox(it)
                },
                onClose = { isBackable ->
                    overlayConfigBoxActionConditionGeneral.hide()
                    if (isBackable) {
                        overlaySelectBoxConditionType.show()
                    }
                }
            )

            overlayConfigBoxActionConditionDeviceState = OverlayConfigBoxActionConditionDeviceState(
                requireActivity(),
                binding.overlayContainer,
                onConfigInput = {
                    overlayConfigInputBoxActionConditionDeviceState.show()
                },
                onBoxActionCondtionDeviceStateCreated = {

                },
                onClose = {

                }
            )

            overlayConfigBoxActionCallHttp = OverlayConfigBoxActionCallHttp(
                requireActivity(),
                binding.overlayContainer,
                onBoxActionCallHttpCreated = {
                    overlayConfigBoxActionCallHttp.hide()
                    configBox(it)
                },
                onClose = {
                    overlayConfigBoxActionCallHttp.hide()
                }
            )

            overlayConfigInputBoxActionConditionDeviceState =
                OverlayConfigInputBoxActionConditionDeviceState(
                    requireActivity(),
                    binding.overlayContainer,
                    onSelectDevice = {
                        overlaySelectDevice.show()
                    },
                    onBoxActionCondtionDeviceStateCreated = {

                    },
                    onClose = {

                    }
                )

            overlaySelectDevice = OverlaySelectDevice(
                requireActivity(),
                binding.overlayContainer,
                onDevicesSelected = {

                },
                onClose = {
                    overlaySelectDevice.hide()
                }
            )
        }
    }

    private fun configBox(fBox: FBox) {
        var highestBoxId: Int = 0
        var highestSegId: Int = 0
        boxes.forEach { currentBox ->
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
                fBox.id = (highestBoxId + 1).toString()
            }

            is FBoxAction -> {
                if (boxes.size == 1) {
                    if (boxes[0] is FBoxEvent) {
                        (boxes[0] as FBoxEvent).targetSegId = (highestSegId + 1).toString()
                    }
                }
                fBox.id = (highestBoxId + 1).toString()
                fBox.segId = (highestSegId + 1).toString()
                fBox.rootId = rootBoxId
                fBox.positiveSegId = ""
                fBox.negativeSegId = ""
                val rootBox = boxes.find { it.id == rootBoxId }
                if (rootBox != null && rootBox is FBoxAction) {
                    when (addBoxType) {
                        LayoutZoomPan.OnBoxActionListener.AddType.DEFAULT,
                        LayoutZoomPan.OnBoxActionListener.AddType.POSITIVE -> {
                            rootBox.positiveSegId = fBox.segId
                        }

                        LayoutZoomPan.OnBoxActionListener.AddType.NEGATIVE -> {
                            rootBox.negativeSegId = fBox.segId
                        }

                        else -> {

                        }
                    }
                }
            }
        }
        boxes.add(fBox)
        boxes.forEach {
            ILogR.D(TAG, "configBox:boxInfo", Gson().toJson(it))
        }
        binding.boxLayout.boxList = ArrayList(boxes)
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