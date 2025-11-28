package com.example.thingsflow.ui.flowScene.fragmentFlowScenario

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogConfigHeaderHttp
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
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
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
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
import rogo.iot.module.base.ILogR

/**
 * @file: is to set up a flow scenario
 * It allows user to:
 * - create boxes(event, action, condition)
 * - set label for flow scenario
 */
@AndroidEntryPoint
class FragmentFlowScenario : FragmentBase<FragmentFlowScenarioBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario

    internal val TAG = "FragmentFlowScenario"

    internal val vmFlowScenario: VMFlowScenario by activityViewModels<VMFlowScenario>()
    internal var currentBoxType: Int = -1

    // Determine if the next box belongs to positive segment or negative segment of the current box
    internal var newSegType: LayoutZoomPan.OnBoxActionListener.NewSegType? = null

    internal lateinit var overlaySelectBoxEventType: OverlaySelectBoxEventType //use when select type of box event
    internal lateinit var overlayConfigBoxEventFromDevice: OverlayConfigBoxEventFromDevice // use when config box event from device
    internal lateinit var overlaySelectBoxType: OverlaySelectBoxType // use when select type of box(action or condition)
    internal lateinit var overlaySelectBoxActionType: OverlaySelectBoxActionType // use when select type of box action(call http, control device, etc)
    internal lateinit var overlaySelectBoxConditionType: OverlaySelectBoxConditionType// use when select type of box condition(condition general, device state, etc...)
    internal lateinit var overlayConfigBoxActionConditionGeneral: OverlayConfigBoxActionConditionGeneral // use when config box action condition general
    internal lateinit var overlayConfigBoxActionConditionDeviceState: OverlayConfigBoxActionConditionDeviceState // use when config box action condition device state
    internal lateinit var overlayConfigInputBoxActionConditionDeviceState: OverlayConfigInputBoxActionConditionDeviceState //use when create an input from a device for box action condition device state
    internal lateinit var overlayConfigBoxActionCallHttp: OverlayConfigBoxActionCallHttp // use when config box action call http
    internal lateinit var overlayConfigBoxActionControlDevice: OverlayConfigBoxActionControlDevice // user when config box action control device
    internal lateinit var overlaySetControlDevice: OverlaySetControlDevice
    internal lateinit var overlaySelectDevice: OverlaySelectDevice // use when select device for boxes

    /*
    * DialogConfigHeaderHttp: is for configuring HTTP headers for box action call http
    * - onHeadersConfiged: triggered when user save the headers successfully
    */
    internal val dialogConfigHeaderHttp: DialogConfigHeaderHttp by lazy {
        DialogConfigHeaderHttp(
            requireActivity(),
            onHeadersConfiged = { headers ->
                dialogConfigHeaderHttp.dismiss()
                overlayConfigBoxActionCallHttp.show(headers)
            },
            onClose = {
                dialogConfigHeaderHttp.dismiss()
                overlayConfigBoxActionCallHttp.show(arrayListOf())
            }
        )
    }

    // set label for the current flow
    internal val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext()
        )
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = context?.getString(R.string.list_of_flow_scenario)

            binding.boxLayout.onBoxClickListener = this@FragmentFlowScenario

            vmFlowScenario.boxes.observe(this@FragmentFlowScenario) {
                boxLayout.boxList = ArrayList(it)
            }

            // Khởi tạo một danh sách FBox mới
            if (vmFlowScenario.boxes.value?.size == 1) {
                val fBox = vmFlowScenario.boxes.value?.get(0)
                if (fBox is FBoxEvent) {
                    if (fBox.targetSegId.isNullOrEmpty() && fBox.id.isNullOrEmpty()) {
                        btnEditScene.gone()
                        boxLayout.isEditMode = true
                        btnEditScene.setImageDrawable(context?.getDrawable(R.drawable.ic_check))
                    } else {
                        btnEditScene.show()
                        boxLayout.isEditMode = false
                        btnEditScene.setImageDrawable(context?.getDrawable(R.drawable.ic_edit))
                    }
                }
            }

        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            handleOverlaySelectTypeBox()
            handleOverlayConfigBoxEvent()
            handleOverlayConfigBoxAction()
            handleOverlayConfigBoxActionCondition()
            handleOverlaySelectDevice()

            handleAddAndRemoveAction()

            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentFlowScenManagement)
            }

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
        }
    }

    // handle when user click on a box
    override fun onBoxClick(box: FBox?) {
        ILogR.D(TAG, "onBoxClick")
        when (box) {
            is FBoxEventDevice -> {
                if (box.id == null) {
                    overlaySelectBoxEventType.show()
                } else {
                    overlayConfigBoxEventFromDevice.show(box)
                }
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
            is FBoxEventStatistic -> {

            }

            is FBoxActionConditionGeneral,
            is FBoxActionConditionTime,
            is FBoxActionConditionDeviceState -> {

            }

            is FBoxActionControlDevice -> {
                if (box.id == null) {
                    overlaySelectBoxActionType.show()
                } else {
                    overlayConfigBoxActionControlDevice.show()
                }
            }
            is FBoxActionAIGPT,
            is FBoxActionAIGemini,
            is FBoxActionCallHttp,
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

    fun handleAddAndRemoveAction() {
        binding.apply {
            boxLayout.onBoxActionListener = object : LayoutZoomPan.OnBoxActionListener {
                override fun onAddBoxPositiveClicked(
                    box: FBox,
                    newSegType: LayoutZoomPan.OnBoxActionListener.NewSegType
                ) {
                    ILogR.D(TAG, "onAddBoxClicked:fBoxId ", box.id)
                    this@FragmentFlowScenario.newSegType = newSegType
                    overlaySelectBoxType.show()
                    when (box) {
                        is FBoxAction -> {
                            vmFlowScenario.setRootBoxId(box.id)
                        }
                    }
                }

                override fun onRemoveBoxClicked(box: FBox) {
                    ILogR.D(TAG, "onRemoveBoxClicked:fBoxId ", box.id)
                }
            }
        }
    }
}