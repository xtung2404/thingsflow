package com.example.thingsflow.ui.flowbinding.overlayBinding

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutOverlayBindingBoxActionControlDeviceBinding
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterConfiguredDeviceAction
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.example.thingsflow.utils.toElmIntArrayMap
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.type.FBoxType
import rogo.iot.module.flowcommon.value.FControlValue

class OverlayBindingBoxActionControlDevice(
    context: Context,
    container: ViewGroup,
    private val onSelectDevice: (devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>) -> Unit,
    private val onSave: (FBoxActionControlDevice) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayBindingBoxActionControlDeviceBinding>(
    context,
    container,
    LayoutOverlayBindingBoxActionControlDeviceBinding::inflate
) {
    private val TAG = "OverlayBindingBoxActionControlDevice"
    private val vmFlowBinding: VMFlowBinding? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowBinding::class.java]
        }
    }
    private var fBoxActionControlDevice: FBoxActionControlDevice?= null
    private var devType: Int = IoTDeviceType.ALL

    private var attrs: IntArray = intArrayOf()
    private var inputFromParentBoxList: List<TFInputBoxValue?> = listOf() // list of input from previous box

    private val adapterInputFromPreviousBox: AdapterInOutput by lazy {
        AdapterInOutput()
    }
    private var deviceActionMap: HashMap<String?, Array<FControlValue>> = hashMapOf()
    private val adapterConfiguredDeviceAction: AdapterConfiguredDeviceAction by lazy {
        AdapterConfiguredDeviceAction(
            onItemDelete = { devId ->

            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlayBindingBoxActionControlDeviceBinding) {
        binding.apply {

        }
    }

    override fun initUI() {
        super.initUI()
        setUpTabs()
        binding.apply {
            btnClose.setOnClickListener {
                onClose.invoke()
            }
        }
        setUpInputLayout()
        setUpConfigLayout()
        setUpOutputLayout()
    }

    private fun setUpInputLayout() {
        binding.apply {
            //set up adapters
            lnInput.lnInputFromPreviousBox.show()
            lnInput.lnInputFromSpecificDevice.gone()
            lnInput.rvInputFromParentBox.adapter = adapterInputFromPreviousBox
        }
    }

    private fun setUpConfigLayout() {
        binding.apply {
            rvDevice.adapter = adapterConfiguredDeviceAction

            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    devType,
                    attrs,
                    deviceActionMap.toElmIntArrayMap()
                )
            }

            lnEmptyDevices.setOnClickListener {
                onSelectDevice.invoke(
                    devType,
                    attrs,
                    deviceActionMap.toElmIntArrayMap()
                )
            }

            btnSave.setOnClickListener {
                fBoxActionControlDevice?.let {
                    onSave.invoke(fBoxActionControlDevice!!)
                }
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            btnOutputClose.setOnClickListener {
                onClose.invoke()
            }

        }
    }

    override fun show() {
        super.show()
        if (vmFlowBinding?.getSelectedBox() != null && vmFlowBinding?.getSelectedBox() is FBoxActionControlDevice) {
            fBoxActionControlDevice = vmFlowBinding!!.getSelectedBox() as FBoxActionControlDevice
        }

        binding.apply {
            fBoxActionControlDevice?.let {
                ILogR.D(TAG, "show:boxInfo", Gson().toJson(fBoxActionControlDevice))
                fBoxActionControlDevice!!.devType.let {
                    devType = fBoxActionControlDevice!!.devType
                }

                fBoxActionControlDevice!!.attrType.let {
                    attrs = intArrayOf(fBoxActionControlDevice!!.attrType)
                }
                deviceActionMap = fBoxActionControlDevice!!.targetControls
                initialize(fBoxActionControlDevice!!.devType, intArrayOf())
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, cmdMap: HashMap<String?, Array<FControlValue>>) {
        binding.apply {
            deviceActionMap = cmdMap
            tabLayout.getTabAt(1)?.select()
            this@OverlayBindingBoxActionControlDevice.deviceActionMap = cmdMap
            initialize(devType, attrs)
        }
    }

    private fun initialize(
        devType: Int?,
        attrs: IntArray?
    ) {
        binding.apply {
            devType?.let {
                txtDeviceType.text = getDeviceTypeLabel(context, devType)
            }
            attrs?.let {
                if(attrs.isNotEmpty()) {
                    txtAttr.text = getAttrLabel(context, attrs.first())
                } else {
                    txtAttr.text = ""
                }
            }
        }

        showInputFromPreviousBox()
        showUIDevicesSelected(deviceActionMap.isNotEmpty())
    }


    private fun showUIDevicesSelected(isSelected: Boolean) {
        binding.apply {
            if (isSelected) {
                lnDevices.show()
                lnEmptyDevices.gone()
                txtNumberOfDevices.text = "${deviceActionMap.size} thiết bị"
                adapterConfiguredDeviceAction.submitList(deviceActionMap.entries.toList())
            } else {
                lnDevices.gone()
                lnEmptyDevices.show()
            }
        }
    }

    private fun showInputFromPreviousBox() {
        binding.apply {
            //get parent box info
            val parentBox = getPreviousBox()
            var previousBoxType = FBoxType.EVT_FROM_DEVICE
            ILogR.D(TAG, "showInputFromPreviousBox:boxInfo", Gson().toJson(parentBox))
            parentBox?.let {
                vmFlowBinding?.getInputsFromParentBox(parentBox)?.let {
                    inputFromParentBoxList = vmFlowBinding?.getInputsFromParentBox(parentBox)!!
                }
                when(parentBox) {
                    is FBoxEventDevice -> {
                        previousBoxType = FBoxType.EVT_FROM_DEVICE
                        val device = FlowSdk.deviceHandler().get(parentBox.devId)
                        ILogR.D(TAG, "showInputFromPreviousBox:deviceInfo", Gson().toJson(device))
                        if (device == null) {
                            lnInput.lnPreviousBoxDevice.gone()
                        } else {
                            val location = FlowSdk.locationHandler().get(device.locationId)
                            lnInput.txtPinputLabel.text = device.label
                            lnInput.txtPinputLocation.text = location.label
                            lnInput.lnPreviousBoxDevice.show()
                        }
                    }
                    is FBoxActionControlDevice -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        previousBoxType = FBoxType.ACT_CONTROL_DEVICE
                    }
                    is FBoxActionCallHttp -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        previousBoxType = FBoxType.ACT_CALL_HTTP
                    }
                    is FBoxActionConditionGeneral -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        previousBoxType = FBoxType.ACT_CONDITION_GENERAL
                    }
                    is FBoxActionConditionDeviceState ->  {
                        lnInput.lnPreviousBoxDevice.gone()
                        previousBoxType = FBoxType.ACT_CONDITION_DEVICE
                    }
                    else -> FBoxType.EVT_FROM_DEVICE
                }
            }
            adapterInputFromPreviousBox.submitList(
                vmFlowBinding?.groupInputs(previousBoxType, inputFromParentBoxList)
            )
            lnInput.lnInputFromPreviousBox.show()
        }
    }

    private fun getPreviousBox(): FBox? {
        val previousBoxId = fBoxActionControlDevice?.rootId
        return vmFlowBinding?.boxes?.value?.find { it.id == previousBoxId }
    }

    private fun setUpTabs() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(
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

    private fun showTab(input: Boolean = false, config: Boolean = false, output: Boolean = false) {
        binding.apply {
            if (input) lnInput.root.show() else lnInput.root.gone()
            if (config) lnConfig.show() else lnConfig.gone()
            if (output) lnOutput.show() else lnOutput.keepScreenOn
        }
    }
}