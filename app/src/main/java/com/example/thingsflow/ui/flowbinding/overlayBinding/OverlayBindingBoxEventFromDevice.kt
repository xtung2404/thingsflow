package com.example.thingsflow.ui.flowbinding.overlayBinding

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutOverlayBindingBoxEventFromDeviceBinding
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice

class OverlayBindingBoxEventFromDevice(
    context: Context,
    container: ViewGroup,
    private val onSelectDevice: (devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) -> Unit,
    private val onSave: (FBoxEventDevice) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayBindingBoxEventFromDeviceBinding>(
    context,
    container,
    LayoutOverlayBindingBoxEventFromDeviceBinding::inflate
) {

    private val vmFlowBinding: VMFlowBinding? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowBinding::class.java]
        }
    }
    private var selectedDevices: HashMap<String?, IntArray> = hashMapOf()
    private var fBoxEventDevice: FBoxEventDevice?= null
    private var devType: Int = IoTDeviceType.ALL
    private val adapterSelectedDevices: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
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
            rvDevice.adapter = adapterSelectedDevices

            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    devType,
                    intArrayOf(),
                    selectedDevices
                )
            }

            btnSave.setOnClickListener {
                val devId = selectedDevices.keys.firstOrNull()
                val elms = selectedDevices.values.firstOrNull()
                val device = FlowSdk.deviceHandler().get(devId)
                fBoxEventDevice?.devType = devType
                fBoxEventDevice?.elms = elms
                fBoxEventDevice?.devId = devId?: ""
                fBoxEventDevice?.eid = device?.eid?: -1
                fBoxEventDevice?.attrTypes = intArrayOf()

                onSave.invoke(fBoxEventDevice!!)
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
        if (vmFlowBinding?.getSelectedBox() != null && vmFlowBinding?.getSelectedBox() is FBoxEventDevice) {
            fBoxEventDevice = vmFlowBinding!!.getSelectedBox() as FBoxEventDevice
        }
        binding.apply {
            fBoxEventDevice.let {
                if (fBoxEventDevice!!.devId.isNotEmpty()) {
                    selectedDevices[fBoxEventDevice!!.devId] = fBoxEventDevice!!.elms
                } else {
                    selectedDevices = hashMapOf()
                }
                initialize(fBoxEventDevice!!.devType, fBoxEventDevice!!.attrTypes, selectedDevices)
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>?) {
        super.show()
        if (vmFlowBinding?.getSelectedBox() != null && vmFlowBinding?.getSelectedBox() is FBoxEventDevice) {
            fBoxEventDevice = vmFlowBinding!!.getSelectedBox() as FBoxEventDevice
        }
        initialize(devType, attrs, devMap)
    }

    fun initialize(devType: Int?, attrs: IntArray?, devMap: HashMap<String?, IntArray>?) {
        binding.apply {
            this@OverlayBindingBoxEventFromDevice.devType = (devType ?: IoTDeviceType.ALL)
            this@OverlayBindingBoxEventFromDevice.selectedDevices = devMap?: hashMapOf()
            txtDeviceType.text = getDeviceTypeLabel(context, devType!!)
            if (selectedDevices.isEmpty()) {
                txtChooseDevice.show()
                rvDevice.gone()
            } else {
                txtChooseDevice.gone()
                rvDevice.show()
                adapterSelectedDevices.submitList(selectedDevices.entries.toList())
            }
        }
    }
}