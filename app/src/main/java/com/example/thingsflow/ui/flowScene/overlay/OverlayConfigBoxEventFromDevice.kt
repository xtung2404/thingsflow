package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.FBox
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
    private val vmFlowScenario: VMFlowScenario? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScenario::class.java]
        }
    }
    private val TAG = "OverlayConfigBoxEventFromDevice"

    // hashmap to check whether attributes is selected or not
    //key: information of attribure. first: attribure, second: label of attribute
    //value: is attribute selected
    private var attrMap: MutableMap<Pair<Int, String>, Boolean> = mutableMapOf()

    // hashmap to store selected devices
    // key: uuid of device, value: selected elements of device
    private var selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    //adapter for selected devices
    private val adapterSelectedDevices: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
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
        selectedDeviceMap.clear()
    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            setUIDevicesSelected(isSelected = false)
            tabLayoutEvtDevice.getTabAt(0)?.select()

            // set up adapters
            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            rvAttr.adapter = adapterAttributes
            rvDevices.adapter = adapterSelectedDevices
            adapterSelectedDevices.submitList(selectedDeviceMap.entries.toList())

            // map every attribute to its label and set it to false(or unselected)
            attrMap =
                getSupportedAttribue().associate {
                    (it to getAttrLabel(context, it)) to false
                }.toMutableMap()
            adapterAttributes.submitList(attrMap.entries.toList())


            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            btnClose.setOnClickListener {
                onClose.invoke(false)
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke(true)
            }

            btnOutputConfig.setOnClickListener {
                tabLayoutEvtDevice.getTabAt(0)?.select()
            }

            tabLayoutEvtDevice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) {
                                lnConfig.show()
                                lnOutput.gone()
                            } else if (tab.position == 1) {
                                lnConfig.gone()
                                lnOutput.show()
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
            )
        }
    }

    override fun initAction() {
        binding.apply {
            btnCreateBox.setOnClickListener {
                val fBox = FBoxEventDevice().apply {
                    devId = if (selectedDeviceMap.isNotEmpty()) selectedDeviceMap.keys.first() else ""
                    devType = spinnerDeviceType.selectedItem as Int
                    attrTypes = getSelectedAttrs()
                }
                onBoxEventCreated.invoke(fBox)
            }


            btnSelectDevice.setOnClickListener {
                ILogR.D(TAG, "btnSelectDevice:onClick", selectedDeviceMap.size)
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

    override fun show() {
        super.show()
    }
    fun show(fBox: FBox?) {
        super.show()
        binding.apply {
            btnBack.gone()
            fBox?.let {
                if (it is FBoxEventDevice) {
                    selectedDeviceMap.clear()
                    if (it.devId.isNotEmpty()) {
                        selectedDeviceMap[it.devId] = it.elms
                    }
                    initialize(it.devType, it.attrTypes, selectedDeviceMap)
                }
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>) {
        binding.apply {
            selectedDeviceMap = devMap
            initialize(devType, attrs, devMap)
        }
    }

    private fun initialize(devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>) {
        binding.apply {
            ILogR.D(TAG, "initialize:size ", devMap.size)
            devType?.let {
                val devTypePos = adapterSpinnerDeviceType.getPosition(it)
                if (devTypePos != -1) {
                    spinnerDeviceType.setSelection(devTypePos)
                }
            }
            selectedDeviceMap = devMap
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
}