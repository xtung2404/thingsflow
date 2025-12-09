package com.example.thingsflow.ui.flowbinding.overlayBinding

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutOverlayBindingBoxEventFromDeviceBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionGeneralBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedDeviceType
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.rogocore.sdk.SmartSdk
import kotlin.collections.get
import kotlin.text.set

class OverlayBindingBoxEventFromDevice(
    context: Context,
    container: ViewGroup,
    private val onSave: (FBoxEventDevice) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayBindingBoxEventFromDeviceBinding>(
    context,
    container,
    LayoutOverlayBindingBoxEventFromDeviceBinding::inflate
) {

    private val selectedDevices: HashMap<String?, IntArray> = hashMapOf()
    private var fBox: FBoxEventDevice?= null
    private var devType: Int = IoTDeviceType.ALL
    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDevicesSelected = {devices->
                selectedDevices.clear()
                selectedDevices.putAll(devices)
            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlayBindingBoxEventFromDeviceBinding) {
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
            rvDevice.adapter = adapterDevices

            btnCreateBox.setOnClickListener {
                val devId = selectedDevices.keys.firstOrNull()
                val elms = selectedDevices.values.firstOrNull()
                fBox?.devType = devType
                fBox?.elms = elms
                fBox?.devId = devId
                onSave.invoke(fBox!!)
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

    fun  show(fBox: FBox?) {
        super.show()
        if (fBox is FBoxEventDevice) {
            this.fBox = fBox
            binding.apply {
                fBox.let {
                    fBox.devType.let {
                        devType = fBox.devType
                        txtDeviceType.text = getDeviceTypeLabel(context, fBox.devType)
                    }
                    selectedDevices.clear()
                    selectedDevices[fBox.devId] = fBox.elms
                    adapterDevices.setSelectedDeviceMap(selectedDevices)
                    adapterDevices.submitList(
                        FlowSdk.deviceHandler().userDevices.filter {
                            if (fBox.devType == IoTDeviceType.ALL) true else it.devType == fBox.devType
                        }
                    )
                }
            }
        }
    }
}