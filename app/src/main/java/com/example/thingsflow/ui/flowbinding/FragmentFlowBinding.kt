package com.example.thingsflow.ui.flowbinding

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowBindingBinding
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterSpinnerBoxEventType
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
import com.example.thingsflow.ui.flowbinding.overlayBinding.OverlayBindingBoxEventFromDevice
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.example.thingsflow.utils.getSupportedDeviceType
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.toImmutableList
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxAction
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
import rogo.iot.module.platform.ILogR
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback

@AndroidEntryPoint
class FragmentFlowBinding : FragmentBase<FragmentFlowBindingBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_binding

    private val TAG = "FragmentFlowBinding"
    var boxes = mutableListOf<FBox>()

    private val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext(),
        )
    }

    private lateinit var overlayBindingBoxEventFromDevice: OverlayBindingBoxEventFromDevice



    override fun initVariable() {
        super.initVariable()
        binding.apply {

        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            // Khởi tạo một danh sách FBox mới
            boxes.clear()
//            if (boxes.isEmpty()) {
//                btnEditScene.visibility = View.GONE
//                boxLayout.isEditMode = true
//            } else {
//                btnEditScene.visibility = View.VISIBLE
//                boxLayout.isEditMode = false
//            }
                btnEditScene.visibility = View.VISIBLE
            val boxEvt = FBoxEventDevice()
            boxEvt.apply {
                targetSegId = "1"
            }
            boxes.clear()
            boxes.add(boxEvt)
            val fActionBox = FBoxActionControlDevice()
            fActionBox.devId = "1222"
            fActionBox.segId = "1"
            boxes.add(fActionBox)

            val fActionBox1 = FBoxActionControlDevice()
            fActionBox1.devId = "1"
            fActionBox1.segId = "1"
            fActionBox1.positiveSegId = "2"
            fActionBox1.negativeSegId = "3"
            boxes.add(fActionBox1)

            val fActionBox2 = FBoxActionControlDevice()
            fActionBox2.devId = "2"
            fActionBox2.segId = "2"
            fActionBox2.positiveSegId = "6"
            fActionBox2.negativeSegId = "7"
            boxes.add(fActionBox2)

            val fActionBox3 = FBoxActionControlDevice()
            fActionBox3.devId = "3"
            fActionBox3.segId = "3"
            fActionBox3.negativeSegId = "4"
            fActionBox3.positiveSegId = "5"
            boxes.add(fActionBox3)

            val fActionBox4 = FBoxActionControlDevice()
            fActionBox4.devId = "4"
            fActionBox4.segId = "4"
            boxes.add(fActionBox4)
//
            val fActionBox5 = FBoxActionControlDevice()
            fActionBox5.devId = "5"
            fActionBox5.segId = "5"
            boxes.add(fActionBox5)

            val fActionBox6 = FBoxActionControlDevice()
            fActionBox6.devId = "6"
            fActionBox6.segId = "6"
            boxes.add(fActionBox6)

            val fActionBox7 = FBoxActionControlDevice()
            fActionBox7.devId = "7"
            fActionBox7.segId = "7"
            boxes.add(fActionBox7)
            boxLayout.boxList = ArrayList(boxes)
            binding.boxLayout.onBoxClickListener = this@FragmentFlowBinding
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
                FlowSdk.flowHandler().createFlowBinding(
                    "68da06666cc540db9dbb9732",
                    "5555",
                    "6666",
                    "labellll",
                    object : SuccessStatusCallback {
                        override fun onSuccess() {
                            ILogR.D(TAG, "onCreateFlowBinding:onSuccess")
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            ILogR.D(TAG, "onCreateFlowBinding:onFailure", p0, p1)

                        }
                    }
                )
                val evtboxes = arrayListOf<FBox>()
                evtboxes.add(boxes.get(0))
                FlowSdk.flowHandler().bindBoxEvent(
                    "68da06666cc540db9dbb9732",
                    "5555",
                    evtboxes,
                    object : SuccessStatusCallback {
                        override fun onSuccess() {
                            ILogR.D(TAG, "onCreateFlowBinding:onSuccess")
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            ILogR.D(TAG, "onCreateFlowBinding:onFailure", p0, p1)
                        }
                    }
                )
                val otherBoxes = arrayListOf<FBox>()
                otherBoxes.addAll(boxes.filter {
                    it is FBoxAction
                })
                FlowSdk.flowHandler().bindOtherBoxes(
                    "68da06666cc540db9dbb9732",
                    "5555",
                    otherBoxes,
                    object : SuccessStatusCallback {
                        override fun onSuccess() {
                            ILogR.D(TAG, "onCreateFlowBinding:onSuccess")
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            ILogR.D(TAG, "onCreateFlowBinding:onFailure", p0, p1)
                        }
                    }
                )
            }
        }

        overlayBindingBoxEventFromDevice = OverlayBindingBoxEventFromDevice(
            requireActivity(),
            binding.overlayBindingContainer,
            onSave = {

            },
            onClose = {
                overlayBindingBoxEventFromDevice.hide()
            }
        )
    }

    override fun onBoxClick(box: FBox?) {
        ILogR.D(TAG, "onBoxClick")
        when (box) {
            is FBoxEventDevice -> {
                overlayBindingBoxEventFromDevice.show()
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