package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.databinding.DialogDeviceListBinding
import com.example.thingsflow.databinding.DialogFlowNodeListBinding
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterFlowNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogFlowNodeList(
    context: Context,
    private val onConnectNewNode: () -> Unit,
    private val onDevicesSelected: (selectedDevices: HashMap<String?, IntArray>) -> Unit
) : DialogBase<DialogFlowNodeListBinding>(
    context,
    R.layout.dialog_flow_node_list
) {
    private val TAG = "DialogFlowNodeList"
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }

    private val selectedDevices: HashMap<String?, IntArray> = hashMapOf()

    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            false,
            onDevicesSelected = { devices ->
                selectedDevices.clear()
                selectedDevices.putAll(devices)
            }
        )
    }

    override fun setupView(binding: DialogFlowNodeListBinding) {
        binding.apply {
            btnExit.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            cbSelectAll.isChecked = false

            cbSelectAll.setOnCheckedChangeListener { buttonView, isChecked ->

            }

            btnAddNode.setOnClickListener {
                dismiss()
                onConnectNewNode.invoke()
            }

            rvFlowNode.adapter = adapterDevices
            val deviceList = vmDevice?.getUserDevices()?.filter {
                it.devType == IoTDeviceType.GATEWAY ||
                        it.devType == IoTDeviceType.MEDIA_BOX ||
                        it.devType == IoTDeviceType.IR_UNIVERSAL
            }
            adapterDevices.submitList(deviceList)

            btnContinue.setOnClickListener {
                dismiss()
                onDevicesSelected.invoke(selectedDevices)
            }
        }
    }
}