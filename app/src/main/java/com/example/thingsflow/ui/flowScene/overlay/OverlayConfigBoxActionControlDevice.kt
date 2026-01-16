package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionControlDeviceBinding
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.viewmodel.VMFlowScene
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterConfiguredDeviceAction
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.ui.adapter.AdapterSpinnerControlAction
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.utils.getControlableDeviceType
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.example.thingsflow.utils.toElmIntArrayMap
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.type.FBoxType
import rogo.iot.module.flowcommon.value.FControlValue

/**
 * @file: This overlay is used to configure a box action control device(FBoxActionControlDevice)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxActionControlDeviceCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxActionControlDevice(
    context: Context,
    container: ViewGroup,
    private val onSelectDevice: (devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>) -> Unit,
    private val onBoxActionControlDeviceCreated: (FBoxActionControlDevice) -> Unit,
    private val onClose: (isBackable: Boolean) -> Unit
) : OverlayBase<LayoutOverlayConfigBoxActionControlDeviceBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionControlDeviceBinding::inflate
) {
    private val TAG = "OverlayConfigBoxActionControlDevice"
    //selectedDeviceMap is to store selected devices

    private val vmFlowScene: VMFlowScene? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScene::class.java]
        }
    }

    private var fBoxActionControlDevice: FBoxActionControlDevice?= null
    private var deviceActionMap: HashMap<String?, Array<FControlValue>> = hashMapOf()
    private var inputFromParentBoxList: List<TFInputBoxValue?> = listOf() // list of input from previous box


    //adapter for select type of device
    private lateinit var adapterSpinnerDeviceType: AdapterSpinnerDeviceType

    //adapter for select type of action(onoff, lock-unlock,etc...)
    private val adapterSpinnerControlAction: AdapterSpinnerControlAction by lazy {
        AdapterSpinnerControlAction(
            context,
            listOf<Int>(
                IoTAttribute.ACT_ONOFF,
                IoTAttribute.ACT_OPEN_CLOSE,
                IoTAttribute.ACT_LOCK_UNLOCK
            )
        )
    }

    private val adapterInputFromPreviousBox: AdapterInOutput by lazy {
        AdapterInOutput()
    }

    private val adapterConfiguredDeviceAction: AdapterConfiguredDeviceAction by lazy {
        AdapterConfiguredDeviceAction(
            onItemDelete = { devId->

            }
        )
    }


    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionControlDeviceBinding) {
        binding.apply {

        }
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
                onClose.invoke(btnBack.isShown)
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

            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    intArrayOf(
                        spinnerControlAction.selectedItem as Int
                    ),
                    deviceActionMap.toElmIntArrayMap()
                )
            }
        }
    }

    private fun setUpConfigLayout() {
        binding.apply {
            rvDevices.adapter = adapterConfiguredDeviceAction
            spinnerControlAction.adapter = adapterSpinnerControlAction

            btnCreateBox.setOnClickListener {
                val selectedDevType = spinnerDeviceType.selectedItem as Int
                val action = spinnerControlAction.selectedItem as Int

                if (fBoxActionControlDevice == null)
                    fBoxActionControlDevice = FBoxActionControlDevice()

                fBoxActionControlDevice!!.devType = selectedDevType
                fBoxActionControlDevice!!.attrType = action
                fBoxActionControlDevice!!.targetControls = deviceActionMap
                onBoxActionControlDeviceCreated.invoke(fBoxActionControlDevice!!)
            }

            cbLater.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbSelectDevice.isChecked = false
                }
            }

            cbSelectDevice.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbLater.isChecked = false
                    if (deviceActionMap.isEmpty()) {
                        onSelectDevice.invoke(
                            spinnerDeviceType.selectedItem as Int,
                            intArrayOf(
                                spinnerControlAction.selectedItem as Int
                            ),
                            deviceActionMap.toElmIntArrayMap()
                        )
                    }
                }
            }

            spinnerControlAction.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val action = spinnerControlAction.selectedItem as Int
                    adapterSpinnerDeviceType = AdapterSpinnerDeviceType(
                        context, getControlableDeviceType(action)
                    )
                    spinnerDeviceType.adapter = adapterSpinnerDeviceType
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            btnOutputClose.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }
        }
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


    override fun show() {
        super.show()
        binding.apply {
            this@OverlayConfigBoxActionControlDevice.deviceActionMap = hashMapOf()
            btnBack.show()
            fBoxActionControlDevice = null
            tabLayout.getTabAt(0)?.select()
            initialize(null, null)
        }
    }

    fun show(fBox: FBoxActionControlDevice) {
        super.show()
        binding.apply {
            btnBack.gone()
            fBoxActionControlDevice = fBox
            deviceActionMap = HashMap(fBox.targetControls)
            initialize(
                fBoxActionControlDevice?.devType,
                if (fBoxActionControlDevice?.attrType != null) intArrayOf(fBoxActionControlDevice?.attrType!!) else intArrayOf()
            )

        }
    }

    fun show(devType: Int?, attrs: IntArray?, cmdMap: HashMap<String?, Array<FControlValue>>) {
        super.show()
        binding.apply {
            tabLayout.getTabAt(1)?.select()
            this@OverlayConfigBoxActionControlDevice.deviceActionMap = cmdMap
            initialize(devType, attrs)
        }
    }

    private fun initialize(
        devType: Int?,
        attrs: IntArray?
    ) {
        binding.apply {
            devType?.let {
                val devTypePos = adapterSpinnerDeviceType.getPosition(it)
                if (devTypePos != -1) {
                    spinnerDeviceType.setSelection(devTypePos)
                }
            }
            attrs?.let {
                if(attrs.isNotEmpty()) {
                    val attrPos = adapterSpinnerControlAction.getPosition(attrs.first())
                    if (attrPos != -1) {
                        spinnerControlAction.setSelection(attrPos)
                    }
                }
            }
        }

        showInputFromPreviousBox()
        setUIDevicesSelected(deviceActionMap.isNotEmpty())
    }

    private fun setUIDevicesSelected(isSelected: Boolean) {
        binding.apply {
            cbLater.isChecked = !isSelected
            cbSelectDevice.isChecked = isSelected
            btnSelectDevice.isEnabled = isSelected
            when (isSelected) {
                true -> {
                    lnEmptyDevices.gone()
                    lnDevices.show()
                    txtNumberOfDevices.text = "${deviceActionMap.size} thiết bị"
                    adapterConfiguredDeviceAction.submitList(deviceActionMap.entries.toList())
                }

                false -> {
                    lnEmptyDevices.show()
                    lnDevices.gone()
                }
            }
        }
    }

    private fun showInputFromPreviousBox() {
        binding.apply {
            //get parent box info
            val parentBox = getPreviousBox()
            var previousBoxType = FBoxType.EVT_FROM_DEVICE
            parentBox?.let {
                vmFlowScene?.getInputsFromParentBox(parentBox)?.let {
                    inputFromParentBoxList = vmFlowScene?.getInputsFromParentBox(parentBox)!!
                }
                 when(parentBox) {
                     is FBoxEventDevice -> {
                         previousBoxType = FBoxType.EVT_FROM_DEVICE
                         val device = FlowSdk.deviceHandler().get(parentBox.devId)
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
                vmFlowScene?.groupInputs(previousBoxType, inputFromParentBoxList)
            )
            lnInput.lnInputFromPreviousBox.show()
        }
    }

    private fun getPreviousBox(): FBox? {
        val previousBoxId = if (fBoxActionControlDevice == null) vmFlowScene?.getRootBoxId() else fBoxActionControlDevice?.rootId
        return vmFlowScene?.boxes?.value?.find { it.id == previousBoxId }
    }
}