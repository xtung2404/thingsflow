package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionControlDeviceBinding
import com.example.thingsflow.module.define.TFElementCmd
import com.example.thingsflow.module.define.TFInputType
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterConfigedDeviceAction
import com.example.thingsflow.ui.adapter.AdapterInput
import com.example.thingsflow.ui.adapter.AdapterSpinnerControlAction
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.utils.getControlableDeviceType
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice

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
    private val onSelectDevice: (devType: Int?, attrs: IntArray?) -> Unit,
    private val onBoxActionControlDeviceCreated: (FBoxActionControlDevice) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionControlDeviceBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionControlDeviceBinding::inflate
) {
    //selectedDeviceMap is to store selected devices

    private val vmFlowScenario: VMFlowScenario? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScenario::class.java]
        }
    }

    private var parentBoxId: String?= null
    private var deviceActionMap: HashMap<String?, ArrayList<TFElementCmd>> = hashMapOf()

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

    private val adapterInput: AdapterInput by lazy {
        AdapterInput(onItemClicked = {

        })
    }

    private val adapterConfigedDeviceAction: AdapterConfigedDeviceAction by lazy {
        AdapterConfigedDeviceAction()
    }


    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionControlDeviceBinding) {
        binding.apply {

        }
    }

    override fun initVariable() {
        super.initVariable()

    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            setUIDevicesSelected(isSelected = false)

            spinnerControlAction.adapter = adapterSpinnerControlAction
            rvInputFromPreviousBox.adapter = adapterInput
            rvDevices.adapter = adapterConfigedDeviceAction

            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnClose.setOnClickListener {
                onClose.invoke()
            }

            btnOutputConfig.setOnClickListener {
                tabLayoutEvtDevice.getTabAt(0)?.select()
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke()
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

            tabLayoutEvtDevice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) {
                                lnInput.show()
                                lnConfig.gone()
                                lnOutput.gone()
                            }
                            else if (tab.position == 1) {
                                lnInput.gone()
                                lnConfig.show()
                                lnOutput.gone()
                            } else {
                                lnInput.gone()
                                lnConfig.gone()
                                lnOutput.show()
                            }

                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                }
            )
        }
    }
    override fun initAction() {
        binding.apply {
            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    intArrayOf(
                        spinnerControlAction.selectedItem as Int
                    )
                )
            }

            btnCreateBox.setOnClickListener {
                val selectedDevType = spinnerDeviceType.selectedItem as Int
                var selectedDevice: Map.Entry<String?, ArrayList<TFElementCmd>>?= null
                if (deviceActionMap.isNotEmpty()) {
                    selectedDevice = deviceActionMap.entries.first()
                }
//                val selectedElm = selectedDevice?.value?.first()
//                val action = selectedDevice?.value?.drop(1)?.toIntArray()
                val fBox = FBoxActionControlDevice().apply {
                    devType = selectedDevType
                    devId = selectedDevice?.key
//                    selectedElm?.let { elms = intArrayOf(it) }
//                    attrValue = action
                }
                onBoxActionControlDeviceCreated.invoke(fBox)
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
                            )
                        )
                    }
                }
            }

        }
    }

    override fun show() {
        super.show()
        binding.apply {
            deviceActionMap.clear()
            adapterConfigedDeviceAction.submitList(deviceActionMap.entries.toList())
            setUIDevicesSelected(isSelected = false)
            tabLayoutEvtDevice.getTabAt(0)?.select()
            parentBoxId = vmFlowScenario?.getRootBoxId()
            val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
            parentBox?.let {
                adapterInput.submitList(vmFlowScenario?.getInputsFromParentBox(it))
            }
        }
    }

    fun show(box: FBox?) {
        binding.apply {
            if (box is FBoxActionControlDevice) {
                parentBoxId = box.id
                val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
                parentBox?.let {
                    adapterInput.submitList(vmFlowScenario?.getInputsFromParentBox(it))
                }
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, cmdMap: HashMap<String?, ArrayList<TFElementCmd>>) {
        super.show()
        binding.apply {
            deviceActionMap = cmdMap
            tabLayoutEvtDevice.getTabAt(1)?.select()
            parentBoxId = vmFlowScenario?.getRootBoxId()
            val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
            parentBox?.let {
                adapterInput.submitList(vmFlowScenario?.getInputsFromParentBox(it))
            }
            this@OverlayConfigBoxActionControlDevice.deviceActionMap = cmdMap
            devType?.let {
                val devTypePos = adapterSpinnerDeviceType.getPosition(it)
                if (devTypePos != -1) {
                    spinnerDeviceType.setSelection(devTypePos)
                }
            }
            setUIDevicesSelected(deviceActionMap.isNotEmpty())
        }
    }

    private fun setUIDevicesSelected(isSelected: Boolean) {
        binding.apply {
            cbLater.isChecked = !isSelected
            btnSelectDevice.isEnabled = isSelected
            when(isSelected) {
                true -> {
                    lnEmptyDevices.gone()
                    lnDevices.show()
                    txtNumberOfDevices.text = "${deviceActionMap.size} thiết bị"
                    adapterConfigedDeviceAction.submitList(deviceActionMap.entries.toList())
                }
                false -> {
                    lnEmptyDevices.show()
                    lnDevices.gone()
                }
            }
        }
    }

}