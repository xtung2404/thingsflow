package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.module.viewmodel.VMFlowScene
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice

/**
 * @file: This overlay is used to configure a box event from device(FBoxActionCallHttp)
 * It allows user to configure:
 * - set a device as a trigger for the flow
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxEventCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxEventFromDevice(
    context: Context,
    container: ViewGroup,
    private val onSelectDevice: (devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>?) -> Unit,
    private val onBoxEventCreated: (FBoxEventDevice) -> Unit,
    private val onClose: (Boolean) -> Unit
) : OverlayBase<LayoutOverlayConfigBoxEventFromDeviceBinding>(
    context,
    container,
    LayoutOverlayConfigBoxEventFromDeviceBinding::inflate
) {
    private val TAG = "OverlayConfigBoxEventFromDevice"
    private val vmFlowScene: VMFlowScene? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScene::class.java]
        }
    }

    private var selectedDeviceType: Int = IoTDeviceType.ALL

    // hashmap to check whether attributes is selected or not
    //key: information of attribure. first: attribure, second: label of attribute
    //value: is attribute selected
    private var attrMap: MutableMap<Pair<Int, String>, Boolean> = mutableMapOf()

    // hashmap to store selected devices
    // key: uuid of device, value: selected elements of device
    private var selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    private var fBoxEventDevice: FBoxEventDevice? = null


    //adapter for selected devices
    private val adapterSelectedDevices: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
    }

    private val adapterOutput: AdapterInOutput by lazy {
        AdapterInOutput()
    }

    // adapter for select device type
    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(
            context, listOf(
                IoTDeviceType.ALL,
                IoTDeviceType.LIGHT,
                IoTDeviceType.SWITCH,
                IoTDeviceType.PLUG,
                IoTDeviceType.CURTAINS,
                IoTDeviceType.DOORLOCK,
                IoTDeviceType.CAMERA,
                IoTDeviceType.SPEAKER,
                IoTDeviceType.MOTOR_CONTROLLER,
                IoTDeviceType.GATE,
                IoTDeviceType.GATEWAY,
                IoTDeviceType.SENSOR_PRESENCE
            )
        )
    }

    // adapter for selecting attribute
    private val adapterAttributes: AdapterAttributes by lazy {
        AdapterAttributes(
            onItemClicked = {
                attrMap[it] = !attrMap[it]!!
                adapterAttributes.notifyDataSetChanged()
            }
        )
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxEventFromDeviceBinding) {
        binding.apply {

        }
    }

    override fun initVariable() {
        super.initVariable()
        selectedDeviceMap = hashMapOf()
    }

    override fun initUI() {
        super.initUI()
        setUpTabs()
        setUIDevicesSelected(isSelected = false)
        binding.apply {

            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            btnClose.setOnClickListener {
                onClose.invoke(false)
            }
        }
        setUpConfigLayout()
        setUpOutputLayout()
    }


    private fun setUpTabs() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) showTab(config = true)
                            else showTab(output = true)
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
            )
        }
    }

    private fun showTab(config: Boolean = false, output: Boolean = false) {
        binding.apply {
            if (config) lnConfig.show() else lnConfig.gone()
            if (output) lnOutput.show() else lnOutput.gone()
        }
    }

    private fun setUpConfigLayout() {
        binding.apply {
            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            rvAttr.adapter = adapterAttributes

            rvDevices.adapter = adapterSelectedDevices
            adapterSelectedDevices.submitList(selectedDeviceMap.entries.toList())

            attrMap =
                getSupportedAttribue().associate {
                    (it to getAttrLabel(context, it)) to false
                }.toMutableMap()
            adapterAttributes.submitList(attrMap.entries.toList())

            btnCreateBox.setOnClickListener {
                val selectedDeviceType = spinnerDeviceType.selectedItem as Int
                val devId = selectedDeviceMap.keys.firstOrNull()
                if (fBoxEventDevice == null) {
                    fBoxEventDevice = FBoxEventDevice()
                }
                val device = FlowSdk.deviceHandler().get(devId)
                fBoxEventDevice?.devId = devId?: ""
                fBoxEventDevice?.devType = selectedDeviceType
                fBoxEventDevice?.attrTypes = getSelectedAttrs()
                fBoxEventDevice?.eid = device?.eid?: -1
                fBoxEventDevice?.elms = selectedDeviceMap[devId]?: intArrayOf()

                onBoxEventCreated.invoke(fBoxEventDevice!!)
            }

            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    getSelectedAttrs(),
                    selectedDeviceMap
                )
            }


            edtAttr.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        s?.let { searchInput ->
                            //filter attributes according to user
                            val searchedList = attrMap.entries
                                .filter { it.key.second.contains(searchInput, true) }
                                .sortedBy { it.key.second }
                                .toMutableList()

                            adapterAttributes.submitList(searchedList)
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
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
                    if (selectedDeviceMap.isEmpty()) {
                        onSelectDevice.invoke(
                            spinnerDeviceType.selectedItem as Int,
                            getSelectedAttrs(),
                            selectedDeviceMap
                        )
                    }
                } else {
                    btnSelectDevice.isEnabled = false
                }
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            rvOutput.adapter = adapterOutput
            btnOutputClose.setOnClickListener {
                onClose.invoke(true)
            }

            btnOutputConfig.setOnClickListener {
                tabLayout.getTabAt(0)?.select()
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            fBoxEventDevice = null
            selectedDeviceMap = hashMapOf()
            selectedDeviceType = IoTDeviceType.ALL
            showOutput(isBoxCreated = false)
        }
    }

    fun show(selectedBox: FBoxEventDevice?) {
        super.show()
        binding.apply {
            btnBack.gone()
            selectedBox?.let {
                selectedDeviceMap = hashMapOf()
                fBoxEventDevice = selectedBox
                selectedDeviceMap = hashMapOf()
                if (selectedBox.devId.isNotEmpty()) {
                    selectedDeviceMap[selectedBox.devId] = selectedBox.elms
                }
                selectedDeviceType = selectedBox.devType
                initialize( selectedBox.attrTypes)
                showOutput(isBoxCreated = true)
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>) {
        binding.apply {
            devType?.let { selectedDeviceType = devType }
            selectedDeviceMap = devMap
            initialize(attrs)
        }
    }

    private fun initialize(attrs: IntArray?) {
        binding.apply {
            ILogR.D(TAG, "initialize:size ", selectedDeviceMap.size)
            val devTypePos = adapterSpinnerDeviceType.getPosition(selectedDeviceType)
            if (devTypePos != -1) {
                spinnerDeviceType.setSelection(devTypePos)
            }
            setUIDevicesSelected(selectedDeviceMap.isNotEmpty())
        }
    }

    private fun getSelectedAttrs(): IntArray {
        return attrMap.filter { it.value }.map { it.key.first }.toIntArray()
    }

    private fun setUIDevicesSelected(isSelected: Boolean) {
        binding.apply {
            cbLater.isChecked = !isSelected
            btnSelectDevice.isEnabled = isSelected
            when(isSelected) {
                true -> {
                    lnEmptyDevices.gone()
                    lnDevices.show()
                    adapterSelectedDevices.submitList(selectedDeviceMap.entries.toList())
                }
                false -> {
                    lnEmptyDevices.show()
                    lnDevices.gone()
                }
            }
        }
    }

    private fun showOutput(isBoxCreated: Boolean) {
        binding.apply {
            when(isBoxCreated) {
                true -> {
                    lnOutputEmpty.gone()
                    lnOutputList.show()
//                    adapterOutput.submitList(vmFlowScenario?.generateOutputs(fBoxEventDevice))
                }
                false -> {
                    lnOutputEmpty.show()
                    lnOutputList.gone()
                }
            }
        }

    }
}