package com.example.thingsflow.ui.flowScene

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
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

    private val TAG = "FragmentFlowScenario"
    private var currentBoxType: Int = -1
    private lateinit var overlaySelectBoxEventType: OverlaySelectBoxEventType //use when select type of box event
    private lateinit var overlayConfigBoxEventFromDevice: OverlayConfigBoxEventFromDevice // use when config box event from device
    private lateinit var overlaySelectBoxType: OverlaySelectBoxType // use when select type of box(action or condition)
    private lateinit var overlaySelectBoxActionType: OverlaySelectBoxActionType // use when select type of box action(call http, control device, etc)
    private lateinit var overlaySelectBoxConditionType: OverlaySelectBoxConditionType// use when select type of box condition(condition general, device state, etc...)
    private lateinit var overlayConfigBoxActionConditionGeneral: OverlayConfigBoxActionConditionGeneral // use when config box action condition general
    private lateinit var overlayConfigBoxActionConditionDeviceState: OverlayConfigBoxActionConditionDeviceState // use when config box action condition device state
    private lateinit var overlayConfigInputBoxActionConditionDeviceState: OverlayConfigInputBoxActionConditionDeviceState //use when create an input from a device for box action condition device state
    private lateinit var overlayConfigBoxActionCallHttp: OverlayConfigBoxActionCallHttp // use when config box action call http

    private lateinit var overlayConfigBoxActionControlDevice: OverlayConfigBoxActionControlDevice // user when config box action control device

    private lateinit var overlaySelectDevice: OverlaySelectDevice // use when select device for boxes

    /*
    * DialogConfigHeaderHttp: is for configuring HTTP headers for box action call http
    * - onHeadersConfiged: triggered when user save the headers successfully
    */
    private val dialogConfigHeaderHttp: DialogConfigHeaderHttp by lazy {
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

    // The UUID of box parent
    private var rootBoxId: String? = null

    // Determine if the next box belongs to positive segment or negative segment of the current box
    private var addBoxType: LayoutZoomPan.OnBoxActionListener.AddType? = null

    // Store all the boxes of the current flow
    val boxes = mutableListOf<FBox>()

    // set label for the current flow
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

            boxLayout.boxList = ArrayList(boxes)
            binding.boxLayout.onBoxClickListener = this@FragmentFlowScenario
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            handleOverlaySelectTypeBox()
            handleOverlayConfigBoxEvent()
            handleOverlayConfigBoxAction()
            handleOverlayConfigBoxActionCondition()

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
                    when(box) {
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

            overlaySelectDevice = OverlaySelectDevice(
                requireActivity(),
                binding.overlayContainer,
                onDevicesSelected = { devType, attrs, devMap ->
                    if (devMap.isNotEmpty()) {
                        overlaySelectDevice.hide()
                        when(currentBoxType) {
                            FTypeAction.ACT_CONTROL_DEVICE -> {
                                overlayConfigBoxActionControlDevice.show(
                                    devType,
                                    attrs,
                                    devMap
                                )
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
    }

    /**
     * Configures a box before adding it to the list of boxes
     * It calculate the next available 'id' and 'segId' based on existing boxes
     * and links FBoxAction boxes to their parent box.
     */
    private fun configBox(fBox: FBox) {
        var highestBoxId: Int = 0
        var highestSegId: Int = 0
        // Iterate through existing boxes to find the maximum 'id' and 'segId'
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
                // set ID for box event
                fBox.id = (highestBoxId + 1).toString()
            }

            is FBoxAction -> {
                if (boxes.size == 1) {
                    if (boxes[0] is FBoxEvent) {
                        (boxes[0] as FBoxEvent).targetSegId = (highestSegId + 1).toString()
                    }
                }
                // set ID for box action
                fBox.id = (highestBoxId + 1).toString()
                // set segmentID for box action
                fBox.segId = (highestSegId + 1).toString()
                // set the id of the parent box for the current box
                fBox.rootId = rootBoxId
                fBox.positiveSegId = ""
                fBox.negativeSegId = ""
                val rootBox = boxes.find { it.id == rootBoxId }
                // determine if the current box belongs to positive segment or negative segment of the parent box
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

    //handle overlays select type of box
    private fun handleOverlaySelectTypeBox() {
        handleOverlaySelectBoxEventType()
        handleOverlaySelectBoxType()
        handleOverlaySelectBoxActionType()
        handleOverlaySelectBoxActionConditionType()
    }

    // handler overlays config box-events
    private fun handleOverlayConfigBoxEvent() {
        handleOverlayConfigBoxEventFromDevice()
    }

    // handler overlays config box-actions
    private fun handleOverlayConfigBoxAction() {
        handleOverlayConfigBoxActionCallHttp()
        handleOverlayConfigBoxActionControlDevice()
    }

    // handler overlays config box-conditions
    private fun handleOverlayConfigBoxActionCondition() {
        handleOverlayConfigBoxActionConditionGeneral()
        handleOverlayConfigBoxActionConditionDeviceState()
    }

    /**
     * handle when user select type of box event
     * - onBoxEventTypeSelected: triggered when type of box event is selected
     */
    private fun handleOverlaySelectBoxEventType() {
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
    private fun handleOverlaySelectBoxType() {
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
    private fun handleOverlaySelectBoxActionType() {
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
    private fun handleOverlaySelectBoxActionConditionType() {
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
    private fun handleOverlayConfigBoxEventFromDevice() {
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
    }

    /**
     * handle when user create a new box action call http
     * - onConfigHeader: triggered when user config HTTP headers
     * - onBoxActionCallHttpCreated: triggered when a new box event is created
     */
    private fun handleOverlayConfigBoxActionCallHttp() {
        overlayConfigBoxActionCallHttp = OverlayConfigBoxActionCallHttp(
            requireActivity(),
            binding.overlayContainer,
            onConfigHeader = {
                overlayConfigBoxActionCallHttp.hide()
                dialogConfigHeaderHttp.show()
            },
            onBoxActionCallHttpCreated = {
                overlayConfigBoxActionCallHttp.hide()
                configBox(it)
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
    private fun handleOverlayConfigBoxActionControlDevice() {
        overlayConfigBoxActionControlDevice = OverlayConfigBoxActionControlDevice(
            requireActivity(),
            binding.overlayContainer,
            onSelectDevice = { devType, attrs ->
                overlayConfigBoxActionControlDevice.hide()
                overlaySelectDevice.show(devType, attrs)
            },
            onBoxActionControlDeviceCreated = {
                overlayConfigBoxActionControlDevice.hide()
                configBox(it)
            },
            onClose = {
                overlayConfigBoxActionControlDevice.hide()
            }
        )
    }

    /**
     * handle when user create a new box action condition general
     * - onBoxActionCondtionGeneralCreated: triggered when a new box action condition general is created
     */
    private fun handleOverlayConfigBoxActionConditionGeneral() {
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
    }

    /**
     * handle when user create a new box action condition general
     */
    private fun handleOverlayConfigBoxActionConditionDeviceState() {
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
                configBox(it)
            },
            onClose = {

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
                    overlayConfigBoxActionConditionDeviceState.show()
                },
                onClose = {
                    overlayConfigInputBoxActionConditionDeviceState.hide()
                }
            )
    }

    // handle when user click on a box
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