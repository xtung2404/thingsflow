package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingflowsdk.core.define.TFComparision
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionDeviceStateBinding
import com.example.thingsflow.module.define.TFCommand
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputSource
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerComparingValue
import com.example.thingsflow.ui.adapter.AdapterSpinnerComparision
import com.example.thingsflow.ui.adapter.AdapterSpinnerInput
import com.example.thingsflow.ui.adapter.AdapterSpinnerInputSource
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice

/**
 * @file: This overlay is used to configure a box action condition device state(FBoxActionConditionDeviceState)
 * It allows user to configure:
 * - compare state of a device or a value from the previous box with a specific state
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onConfigInput: triggered when user want to create input from other devices
 * @param onBoxActionCondtionDeviceStateCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxActionConditionDeviceState(
    context: Context,
    container: ViewGroup,
    private val onConfigInput: () -> Unit,
    private val onBoxActionCondtionDeviceStateCreated: (FBoxActionConditionDeviceState) -> Unit,
    private val onClose: (Boolean) -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionConditionDeviceStateBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionConditionDeviceStateBinding::inflate
) {
    private val TAG = "OverlayConfigBoxActionConditionDeviceState"
    private val vmFlowScenario: VMFlowScenario? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScenario::class.java]
        }
    }

    //key: deviceUUID, value: selected elements of device
    private var selectedDeviceInputs: HashMap<String?, IntArray> = hashMapOf() // list of device that is configured as a input for box condition device state
    private var inputFromParentBoxList: List<Pair<TFInOutType, Int>>? = listOf() // list of input from previous box
    private var inputFromOtherDevicesList: List<Pair<TFInOutType, Int>>? = listOf()  // list of input that is generated from other devices
    private val adapterSelectedDevice: AdapterSelectedDevice by lazy { // adapter to show list of selected devices as inputs
        AdapterSelectedDevice()
    }
    private val adapterInOutputFromPreviousBox: AdapterInOutput by lazy { AdapterInOutput() } // adapter to show list of input from previous box
    private val adapterInOutputOtherDevices: AdapterInOutput by lazy { AdapterInOutput() } // adapter to show list of input from previous box
    private lateinit var adapterSpinnerInputSource: AdapterSpinnerInputSource // adapter to choose input source: from previous box or from other devices
    private lateinit var adapterSpinnerComparedValue: AdapterSpinnerInput // adapter to let user choose which input needs comparing
    private lateinit var adapterSpinnerComparingValue: AdapterSpinnerComparingValue //adapter to let user choose which value to compare

    // adapter to let user choose which comparision type to use(equal or different)
    private val adapterSpinnerComparision: AdapterSpinnerComparision by lazy {
        AdapterSpinnerComparision(
            context,
            listOf<Int>(
                TFComparision.EQUAL,
                TFComparision.DIFF
            )
        )
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionConditionDeviceStateBinding) {
        binding.apply {

        }
    }

    override fun initUI() {
        super.initUI()
        setUpTabs()
        binding.apply {
            txtDeviceType.text = ""
            txtAttr.text = ""

            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            btnClose.setOnClickListener {
                onClose.invoke(false)
            }
        }
        setUpInputLayout()
        setUpConfigLayout()
        setUpOutputLayout()
    }

    private fun setUpInputLayout() {
        binding.apply {
            rvSelectedDevices.adapter = adapterSelectedDevice
            rvInputFromParentBox.adapter = adapterInOutputFromPreviousBox
            rvOtherInputs.adapter = adapterInOutputOtherDevices

            btnConfigInput.setOnClickListener {
                onConfigInput.invoke()
            }
        }
    }

    private fun setUpConfigLayout() {
        binding.apply {
            spinnerComparisionType.adapter = adapterSpinnerComparision

            btnCreateBox.setOnClickListener {
                createBoxActionConditionDeviceState()
            }

            spinnerInputSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val inputSource = parent?.getItemAtPosition(position) as Int
                    handleInputSourceChanged(inputSource)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerComparedValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val comparedValue = parent?.getItemAtPosition(position) as Pair<TFInOutType, Int>
                    handleComparedValueChanged(comparedValue)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    binding.lnConfigComparedValue.gone()
                }
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            btnOutputClose.setOnClickListener {
                onClose.invoke(false)
            }
        }
    }

    private fun setUpTabs() {
        binding.apply {
            tabLayoutEvtDevice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) showTab(input = true)
                            else if (tab.position == 1) showTab(config = true)
                            else showTab(output = true)
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
            )
        }
    }
    override fun show() {
        super.show()
        binding.apply {
            tabLayoutEvtDevice.getTabAt(0)?.select()
            showInputFromPreviousBox()
            showInputFromOtherDevicesSelected(false, null, null, hashMapOf())
        }
    }

    fun show(devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        super.show()
        binding.apply {
            selectedDeviceInputs = selectedDevices
            devType?.let {
                txtDeviceType.text = getDeviceTypeLabel(context, it)
            }
            showInputFromOtherDevicesSelected(true, devType, attrs, selectedDevices)
        }
    }

    /**
    * show list of input from previous box
     * logic:
     * - get information of previous box
     * - check box type, only when previous box is box event device, the input from previous box can be shown
     */
    private fun showInputFromPreviousBox() {
        binding.apply {
            //get parent box info
            val parentBoxId = vmFlowScenario?.getRootBoxId()
            val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
            val inputSourceList: ArrayList<Int> = arrayListOf()

            parentBox?.let {
                when(parentBox) {
                    is FBoxEventDevice -> {
                        // get list of input from previous box
                        inputFromParentBoxList = vmFlowScenario?.getInputsFromParentBox(parentBox)
                        adapterInOutputFromPreviousBox.submitList(inputFromParentBoxList)
                        lnInputFromPreviousBox.show()
                        inputSourceList.addAll(
                            listOf(
                                TFInputSource.INPUT_FROM_PREVIOUS_BOX,
                                TFInputSource.INPUT_FROM_OTHER_DEVICES
                            )
                        )
                    }
                    else -> {
                        lnInputFromPreviousBox.gone()
                        inputSourceList.addAll(
                            listOf(
                                TFInputSource.INPUT_FROM_OTHER_DEVICES
                            )
                        )
                    }
                }
            }
            adapterSpinnerInputSource = AdapterSpinnerInputSource(
                context,
                inputSourceList
            )
            spinnerInputSource.adapter = adapterSpinnerInputSource
        }
    }

    /**
     * handle views when the inputs from other devices are configured or not
     * @param isSelected: ```Boolean``` the inputs from other devices are configured or not
     * @param devType: ```Int``` type of device
     * @param attrs: ```IntArray``` list of attributes
     * @param selectedDevices: ```HashMap<String?, IntArray>``` list of selected devices . key: uuid of device, value: selected elements of device
     */
    private fun showInputFromOtherDevicesSelected(isSelected: Boolean, devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        binding.apply {
            when(isSelected) {
                true -> {
                    //if the devices are configured as input, show list of available inputs
                    btnConfigInput.gone()
                    lnInputConfigured.show()
                    adapterSelectedDevice.submitList(selectedDevices.entries.toList())
                    inputFromOtherDevicesList =
                        vmFlowScenario?.generateInputsFromSpecificDevices(
                            devType,
                            attrs,
                            selectedDevices
                        )?.filter { it.first == TFInOutType.PAYLOAD }
                    adapterInOutputOtherDevices.submitList(inputFromOtherDevicesList)
                }
                else -> {
                    btnConfigInput.show()
                    lnInputConfigured.gone()
                }
            }
        }
    }

    /**
     * show list of compared values when souce from spinner input source is changed
     */
    private fun handleInputSourceChanged(source: Int) {
        binding.apply {
            val comparedValueList = if (source == TFInputSource.INPUT_FROM_PREVIOUS_BOX) inputFromParentBoxList else inputFromOtherDevicesList
            if (comparedValueList.isNullOrEmpty()) {
                lnConfigComparedValue.gone()
            } else {
                lnConfigComparedValue.show()
                adapterSpinnerComparedValue = AdapterSpinnerInput(
                    context,
                    comparedValueList
                )
                spinnerComparedValue.adapter = adapterSpinnerComparedValue
            }
        }
    }

    /**
     * show list of comparing values when spinner compared value is changed
     */
    private fun handleComparedValueChanged(comparedValue: Pair<TFInOutType, Int>) {
        binding.apply {
            adapterSpinnerComparingValue = AdapterSpinnerComparingValue(
                context,
                generateComparingValues(comparedValue)
            )
            spinnerComparingValue.adapter = adapterSpinnerComparingValue
        }
    }

    /**
     * generate list of comparing values based on input type and attribute type
     */
    private fun generateComparingValues(comparedValue: Pair<TFInOutType, Int>): List<Pair<TFInOutType, IntArray>> {
        val values = mutableListOf<Pair<TFInOutType, IntArray>>()

        when(comparedValue.first) {
            TFInOutType.PAYLOAD -> {
                val selectedAttr = comparedValue.second
                TFCommand.getCmdByAttr(selectedAttr).forEach { command ->
                    values.add(Pair(TFInOutType.PAYLOAD, command.cmd))
                }
            }

            else -> {

            }
        }
        return values
    }

    private fun createBoxActionConditionDeviceState() {
        binding.apply {
            val fBox = FBoxActionConditionDeviceState().apply {
                val selectedComparedValue = spinnerComparedValue.selectedItem as Pair<TFInOutType, Int>
                when(spinnerInputSource.selectedItem as Int) {
                    TFInputSource.INPUT_FROM_PREVIOUS_BOX -> {
                        isInputFromPreviousBox = true
                        val comparisionType = spinnerComparisionType.selectedItem as Int
                        val selectedComparingValue = spinnerComparingValue.selectedItem as Pair<TFInOutType, IntArray>

                        when(selectedComparedValue.first) {
                            TFInOutType.PAYLOAD -> {
                                val parentBoxId = vmFlowScenario?.getRootBoxId()
                                val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
                                parentBox?.let {
                                    if (parentBox is FBoxEventDevice) {
                                        attrType = selectedComparedValue.second
                                        condition = comparisionType
//                                        if (selectedComparingValue.second.isNotEmpty() && selectedComparingValue.second.size >= 2) {
//                                            comparingValue = arrayOf<InputSource>(
//                                                InputSource(
//                                                    InputSourceType.INTEGER,
//                                                    selectedComparingValue.second[1]
//                                                )
//                                            )
//                                        }

                                        val device = FlowSdk.deviceHandler().get(parentBox.devId)
                                        device?.let {
                                            devId = device.uuid
                                            eid = device.eid
//                                            elm = device.elementInfos.filter {
//                                                it.value.attrInfos.contains(selectedComparedValue.second)
//                                            }.entries.first().key
                                        }
                                    }
                                }
                                if (selectedDeviceInputs.isNotEmpty()) {
                                    val deviceEntry = selectedDeviceInputs.entries.first()
                                    val device = FlowSdk.deviceHandler().get(deviceEntry.key)
                                    val selectedElms: IntArray = deviceEntry.value
                                    val comparisionType = spinnerComparisionType.selectedItem as Int
                                    val selectedComparingValue = spinnerComparingValue.selectedItem as Pair<TFInOutType, IntArray>

                                    device?.let {
                                        devId = device.uuid
                                        eid = device.eid
                                        elm = if (selectedElms.isNotEmpty()) selectedElms.first() else device.elementIds.first()
                                        attrType = selectedComparedValue.second
                                        isInputFromPreviousBox = false
                                        condition = comparisionType
//                                        if (selectedComparingValue.second.isNotEmpty() && selectedComparingValue.second.size >= 2) {
//                                            comparingValue = arrayOf<InputSource>(
//                                                InputSource(
//                                                    InputSourceType.INTEGER,
//                                                    selectedComparingValue.second[1]
//                                                )
//                                            )
//                                        }
                                    }

                                }
                            }
                            else -> {

                            }
                        }
                    }
                    else -> {
                        when(selectedComparedValue.first) {
                            TFInOutType.PAYLOAD -> {
                                if (selectedDeviceInputs.isNotEmpty()) {
                                    val deviceEntry = selectedDeviceInputs.entries.first()
                                    val device = FlowSdk.deviceHandler().get(deviceEntry.key)
                                    val selectedElms: IntArray = deviceEntry.value
                                    val comparisionType = spinnerComparisionType.selectedItem as Int
                                    val selectedComparingValue = spinnerComparingValue.selectedItem as Pair<TFInOutType, IntArray>

                                    device?.let {
                                        devId = device.uuid
                                        eid = device.eid
                                        elm = if (selectedElms.isNotEmpty()) selectedElms.first() else device.elementIds.first()
                                        attrType = selectedComparedValue.second
                                        isInputFromPreviousBox = false
                                        condition = comparisionType
                                        if (selectedComparingValue.second.isNotEmpty() && selectedComparingValue.second.size >= 2) {
//                                            comparingValue = arrayOf<InputSource>(
//                                                InputSource(
//                                                    InputSourceType.INTEGER,
//                                                    selectedComparingValue.second[1]
//                                                )
//                                            )
                                        }
                                    }

                                }
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
            onBoxActionCondtionDeviceStateCreated.invoke(fBox)
        }
    }

    private fun showTab(input: Boolean = false, config: Boolean = false, output: Boolean = false) {
        binding.apply {
            if (input) lnInput.show() else lnInput.gone()
            if (config) lnConfig.show() else lnConfig.gone()
            if (output) lnOutput.show() else lnOutput.keepScreenOn
        }
    }
}