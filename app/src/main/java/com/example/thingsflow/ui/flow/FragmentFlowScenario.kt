package com.example.thingsflow.ui.flow

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterSpinnerBoxEventType
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.customview.LayoutZoomPan
import com.example.thingsflow.ui.customview.ViewBox
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.ui.dialog.DialogLabelFlowScenario
import com.example.thingsflow.utils.FBoxEventType
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.example.thingsflow.utils.getSupportedDeviceType
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
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk
import java.util.Arrays

@AndroidEntryPoint
class FragmentFlowScenario : FragmentBase<FragmentFlowScenarioBinding>(),
    ViewBox.OnBoxClickListener {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario

    private val TAG = "FragmentFlowScenario"
    val boxes = mutableListOf<FBox>()
    private var attrMap: MutableMap<Pair<Int, String>, Boolean>  = mutableMapOf()

    private val adapterSpinnerBoxEventType: AdapterSpinnerBoxEventType by lazy {
        AdapterSpinnerBoxEventType(requireContext(), getSupportedBoxEvent())
    }
    private val dialogLabelFlowScenario: DialogLabelFlowScenario by lazy {
        DialogLabelFlowScenario(
            requireContext(),
            requireActivity()
        )
    }

    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(requireContext(), getSupportedDeviceType())
    }

    private val adapterAttributes: AdapterAttributes by lazy {
        AdapterAttributes(
            onItemClicked = {
                attrMap[it] = !attrMap[it]!!
                adapterAttributes.notifyDataSetChanged()
            }
        )
    }

    private val dialogDeviceList: DialogDeviceList by lazy {
        DialogDeviceList(
            requireContext(),
            requireActivity(),
            onDeviceSelected = {
                binding.apply {
                    txtDeviceLabel.text = SmartSdk.deviceHandler().get(it.first)?.label
                }
            }
        )
    }

    override fun initVariable() {
        super.initVariable()
        binding.apply {
            overlayBoxEvent.visibility = View.GONE
            spinnerEventInput.adapter = adapterSpinnerBoxEventType
            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            rvAttr.adapter = adapterAttributes
            attrMap = getSupportedAttribue()
                .map { (it to getAttrLabel(requireContext(), it)) to false }
                .toMap()
                .toMutableMap()
            adapterAttributes.submitList(attrMap.entries.toList())
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            cbLater.isChecked = true
            btnSelectDevice.isEnabled = false
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
            handleOverlayEventAction()

            btnUpdateLabel.setOnClickListener {
                dialogLabelFlowScenario.show()
            }

            btnEditScene.setOnClickListener {
                boxLayout.isEditMode = !boxLayout.isEditMode
            }


            btnEvtConfig.setOnClickListener {
                when (spinnerEventInput.selectedItem as Int) {
                    FBoxEventType.FBOX_EVENT_TYPE_DEVICE -> {
                        val boxEvt = FBoxEventDevice()
                        boxEvt.apply {
                            devType = spinnerDeviceType.selectedItem as Int
                            attrTypes = attrMap
                                .filter { it.value }
                                .keys
                                .map { it.first }.toIntArray()
                            targetSegId = 1
                        }
                        boxes.clear()
                        boxes.add(boxEvt)
                        val fActionBox = FBoxActionControlDevice()
                        fActionBox.devId = "1222"
                        fActionBox.segId = 1
                        boxes.add(fActionBox)

                        val fActionBox1 = FBoxActionControlDevice()
                        fActionBox1.devId = "1"
                        fActionBox1.segId = 1
                        fActionBox1.positiveSegId = 2
                        fActionBox1.negativeSegId = 3
                        boxes.add(fActionBox1)

                        val fActionBox2 = FBoxActionControlDevice()
                        fActionBox2.devId = "2"
                        fActionBox2.segId = 2
                        fActionBox2.positiveSegId = 6
                        fActionBox2.negativeSegId = 7
                        boxes.add(fActionBox2)

                        val fActionBox3 = FBoxActionControlDevice()
                        fActionBox3.devId = "3"
                        fActionBox3.segId = 3
                        fActionBox3.negativeSegId = 4
                        fActionBox3.positiveSegId = 5
                        boxes.add(fActionBox3)

                        val fActionBox4 = FBoxActionControlDevice()
                        fActionBox4.devId = "4"
                        fActionBox4.segId = 4
                        boxes.add(fActionBox4)
//
                        val fActionBox5 = FBoxActionControlDevice()
                        fActionBox5.devId = "5"
                        fActionBox5.segId = 5
                        boxes.add(fActionBox5)

                        val fActionBox6 = FBoxActionControlDevice()
                        fActionBox6.devId = "6"
                        fActionBox6.segId = 6
                        boxes.add(fActionBox6)

                        val fActionBox7 = FBoxActionControlDevice()
                        fActionBox7.devId = "7"
                        fActionBox7.segId = 7
                        boxes.add(fActionBox7)
                        boxLayout.boxList = ArrayList(boxes)
                        handleOverlayAnimation(false)
                    }
                }
                btnEditScene.visibility = View.VISIBLE
            }
        }
    }

    private fun handleOverlayEventAction() {
        binding.apply {
            btnSelectDevice.setOnClickListener {
                dialogDeviceList.show()
            }

            btnBackOverlayEvent.setOnClickListener {
                handleOverlayAnimation(false)

            }

            edtAttr.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        s?.let {searchInput ->
                            //filter attributes according to user
                            val searchedList = attrMap.entries
                                .filter { it.key.second.contains(searchInput, true) }
                                .sortedBy { it.key.second }
                                .toMutableList()

                            adapterAttributes.submitList(searchedList)
                        }

                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                }
            )

            cbLater.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbSelectDevice.isChecked = false
                    btnSelectDevice.isEnabled = false
                }
            }

            cbSelectDevice.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbLater.isChecked = false
                    btnSelectDevice.isEnabled = true
                } else {
                    btnSelectDevice.isEnabled = false
                }
            }
            boxLayout.onBoxActionListener = object : LayoutZoomPan.OnBoxActionListener {
                override fun onAddBoxClicked(box: FBoxActionControlDevice) {
                    // ✅ biết box nào vừa bấm nút "+"
                    Toast.makeText(context, "Add vào box ${box.devId}", Toast.LENGTH_SHORT).show()
                }

                override fun onRemoveBoxClicked(box: FBoxActionControlDevice) {
                    // ✅ biết box nào vừa bấm nút "-"
                    Toast.makeText(context, "Remove box ${box.devId}", Toast.LENGTH_SHORT).show()
                }
            }
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
                binding.overlayBoxEvent.visibility = View.VISIBLE
                handleOverlayAnimation(true)
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

    /**
     * @param isOpen: Boolean indicates if to show the overlay event or hide
     */
    fun handleOverlayAnimation(isOpen: Boolean) {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        if (isOpen) {
            binding.overlayBoxEvent.visibility = View.VISIBLE
            binding.overlayBoxEvent.translationX = screenWidth

            binding.overlayBoxEvent.animate()
                .translationX(0f)
                .setDuration(300)
                .start()

        } else {
            binding.overlayBoxEvent.animate()
                .translationX(screenWidth)
                .setDuration(300)
                .withEndAction {
                    binding.overlayBoxEvent.visibility = View.GONE
                    binding.overlayBoxEvent.translationX = 0f
                }
                .start()
        }
    }
}