package com.example.thingsflow.ui.flowbinding.overlayBinding

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutOverlayBindingBoxActionControlDeviceBinding
import com.example.thingsflow.databinding.LayoutOverlayBindingBoxEventFromDeviceBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionGeneralBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterConfiguredDeviceAction
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedDeviceType
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.example.thingsflow.utils.toElmIntArrayMap
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.value.FControlValue
import rogo.iot.module.rogocore.sdk.SmartSdk
import kotlin.collections.get
import kotlin.text.set

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
    private val vmFlowBinding: VMFlowBinding? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowBinding::class.java]
        }
    }
    private var fBoxActionControlDevice: FBoxActionControlDevice?= null
    private var devType: Int = IoTDeviceType.ALL

    private var attrs: IntArray = intArrayOf()

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
            btnCreateBox.setOnClickListener {

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
                fBoxActionControlDevice!!.devType.let {
                    devType = fBoxActionControlDevice!!.devType
                }

                fBoxActionControlDevice!!.attrType.let {
                    attrs = intArrayOf(fBoxActionControlDevice!!.attrType)
                }
                deviceActionMap = fBoxActionControlDevice!!.targetControls
                initialize()
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, devActionMap: HashMap<String?, Array<FControlValue>>) {

    }

    fun initialize() {
        binding.apply {
            txtDeviceType.text = getDeviceTypeLabel(context, devType)
            showUIDevicesSelected(deviceActionMap.isNotEmpty())
        }
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
}