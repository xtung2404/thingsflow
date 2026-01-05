package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingflowsdk.core.base.define.TFComparision
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionDeviceStateBinding
import com.example.thingsflow.module.define.TFCommand
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputBoxValue
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
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.value.FInputValue
import rogo.iot.module.flowcommon.value.FInputValueType

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
    private val onConfigInput: (devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>) -> Unit,
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

    private var fBoxActionConditionDeviceState: FBoxActionConditionDeviceState?= null
    private var selectedDevType: Int = IoTDeviceType.ALL
    private var selectedAttrs: IntArray = intArrayOf()

    //key: deviceUUID, value: selected elements of device
    private var selectedDeviceInputs: HashMap<String?, IntArray> = hashMapOf() // list of device that is configured as a input for box condition device state
    private var inputFromParentBoxList: List<TFInputBoxValue>? = listOf() // list of input from previous box
    private var inputFromOtherDevicesList: List<TFInputBoxValue>? = listOf()  // list of input that is generated from other devices
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
            lnInput.rvSelectedDevices.adapter = adapterSelectedDevice
            lnInput.rvInputFromParentBox.adapter = adapterInOutputFromPreviousBox
            lnInput.rvOtherInputs.adapter = adapterInOutputOtherDevices

            lnInput.btnConfigInput.setOnClickListener {
                onConfigInput.invoke(
                    selectedDevType,
                    selectedAttrs,
                    selectedDeviceInputs
                )
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
                    val comparedValue = parent?.getItemAtPosition(position) as TFInputBoxValue
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
            fBoxActionConditionDeviceState = null

            selectedDeviceInputs = hashMapOf()
            selectedDevType = IoTDeviceType.ALL
            selectedAttrs = intArrayOf()

            tabLayoutEvtDevice.getTabAt(0)?.select()

            showInputFromPreviousBox()
            showInputFromOtherDevicesSelected(false)
        }
    }

    fun show(fBox: FBoxActionConditionDeviceState?) {
        super.show()
        fBoxActionConditionDeviceState = fBox
        binding.apply {

        }
    }

    fun show(devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        super.show()
        binding.apply {
            selectedDeviceInputs = selectedDevices
            selectedDevType = devType ?: IoTDeviceType.ALL
            selectedAttrs = attrs ?: intArrayOf()

            lnInput.txtDeviceType.text = getDeviceTypeLabel(context, selectedDevType)
            showInputFromOtherDevicesSelected(true)
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
            val parentBox = getPreviousBox()
            val inputSourceList: ArrayList<Int> = arrayListOf()
            parentBox?.let {
                when(parentBox) {
                    is FBoxEventDevice -> {
                        // get list of input from previous box
                        val device = FlowSdk.deviceHandler().get(parentBox.devId)
                        device?.let {
                            val location = FlowSdk.locationHandler().get(device.locationId)
                            lnInput.txtPinputLabel.text = device.label
                            lnInput.txtPinputLocation.text = location.label
                        }
                        inputFromParentBoxList = vmFlowScenario?.getInputsFromParentBox(parentBox)

                        adapterInOutputFromPreviousBox.submitList(
                            vmFlowScenario?.groupInputs(inputFromParentBoxList)
                        )

                        lnInput.lnInputFromPreviousBox.show()
                        inputSourceList.addAll(
                            listOf(
                                TFInputSource.INPUT_FROM_PREVIOUS_BOX,
                                TFInputSource.INPUT_FROM_OTHER_DEVICES
                            )
                        )
                    }
                    else -> {
                        lnInput.lnInputFromPreviousBox.gone()
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

    private fun getPreviousBox(): FBox? {
        val parentBoxId = vmFlowScenario?.getRootBoxId()
        return vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
    }

    /**
     * handle views whether the inputs from other devices are configured or not
     * @param isSelected: ```Boolean``` the inputs from other devices are configured or not
     * @param devType: ```Int``` type of device
     * @param attrs: ```IntArray``` list of attributes
     * @param selectedDevices: ```HashMap<String?, IntArray>``` list of selected devices . key: uuid of device, value: selected elements of device
     */
    private fun showInputFromOtherDevicesSelected(isSelected: Boolean) {
        binding.apply {
            when(isSelected) {
                true -> {
                    //if the devices are configured as input, show list of available inputs
                    lnInput.btnConfigInput.gone()
                    lnInput.lnInputConfigured.show()
                    adapterSelectedDevice.submitList(selectedDeviceInputs.entries.toList())

                    inputFromOtherDevicesList =
                        vmFlowScenario?.generateInputsFromSpecificDevices(
                            selectedDevType,
                            selectedAttrs,
                            selectedDeviceInputs
                        )?.filter { it.inputType == TFInOutType.PAYLOAD_STATE }

                    adapterInOutputOtherDevices.submitList(
                        vmFlowScenario?.groupInputs(inputFromOtherDevicesList)
                    )

                }
                else -> {
                    lnInput.btnConfigInput.show()
                    lnInput.lnInputConfigured.gone()
                }
            }
        }
    }

    /**
     * show list of compared values when source from spinner input source is changed
     */
    private fun handleInputSourceChanged(source: Int) {
        binding.apply {
            val comparedValueList = if (source == TFInputSource.INPUT_FROM_PREVIOUS_BOX) inputFromParentBoxList else inputFromOtherDevicesList
            ILogR.D(TAG, "handleInputSourceChanged:comparedValueListSize=", comparedValueList?.size)
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
    private fun handleComparedValueChanged(comparedValue: TFInputBoxValue) {
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
    private fun generateComparingValues(comparedValue: TFInputBoxValue): List<Pair<TFInOutType, IntArray>> {
        val values = mutableListOf<Pair<TFInOutType, IntArray>>()

        when(comparedValue.inputType) {
            TFInOutType.PAYLOAD_STATE -> {
                val selectedAttr = comparedValue.value as Int
                TFCommand.getCmdByAttr(selectedAttr).forEach { command ->
                    values.add(Pair(TFInOutType.PAYLOAD_STATE, command.cmd))
                }
            }

            else -> {

            }
        }
        return values
    }

    private fun createBoxActionConditionDeviceState() {
        binding.apply {
            if (fBoxActionConditionDeviceState == null) {
                fBoxActionConditionDeviceState = FBoxActionConditionDeviceState()
            }
            val selectedComparedValue = spinnerComparedValue.selectedItem as TFInputBoxValue
            val comparisionType = spinnerComparisionType.selectedItem as Int
            val selectedComparingValue = spinnerComparingValue.selectedItem as Pair<TFInOutType, IntArray>

            fBoxActionConditionDeviceState?.attrType = selectedComparedValue.value as Int
            fBoxActionConditionDeviceState?.condition = comparisionType
            fBoxActionConditionDeviceState?.isInputFromPreviousBox = spinnerInputSource.selectedItem as Int == TFInputSource.INPUT_FROM_PREVIOUS_BOX
            when(selectedComparedValue.inputType) {
                TFInOutType.PAYLOAD_STATE -> {
                    val device = FlowSdk.deviceHandler().get(selectedComparedValue.devId)
                    device?.let {
                        fBoxActionConditionDeviceState?.devId = device.uuid
                        fBoxActionConditionDeviceState?.eid = device.eid
                        fBoxActionConditionDeviceState?.elm = selectedComparedValue.elm
                    }
                    if (selectedComparingValue.second.isNotEmpty() && selectedComparingValue.second.size >= 2) {
                        fBoxActionConditionDeviceState?.comparingValue = arrayOf<FInputValue>(
                            FInputValue(
                                FInputValueType.INTEGER,
                                selectedComparingValue.second[1]
                            )
                        )
                    }
                }
                else -> {

                }
            }
            onBoxActionCondtionDeviceStateCreated.invoke(fBoxActionConditionDeviceState!!)
        }
    }

    private fun showTab(input: Boolean = false, config: Boolean = false, output: Boolean = false) {
        binding.apply {
            if (input) lnInput.root.show() else lnInput.root.gone()
            if (config) lnConfig.show() else lnConfig.gone()
            if (output) lnOutput.show() else lnOutput.keepScreenOn
        }
    }
}