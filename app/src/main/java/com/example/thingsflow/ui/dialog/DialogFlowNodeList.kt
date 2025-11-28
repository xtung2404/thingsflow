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
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogFlowNodeList(
    context: Context,
    private val onConnectNewNode: () -> Unit,
    private val onDevicesSelected: (List<IoTDevice>) -> Unit
): DialogBase<DialogFlowNodeListBinding>(
    context,
    R.layout.dialog_flow_node_list
)  {
    private val TAG = "DialogFlowNodeList"
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }
    private var flowNodeMap: MutableMap<IoTDevice, Boolean> = mutableMapOf()
    private val adapterFlowNode: AdapterFlowNode by lazy {
        AdapterFlowNode(
            onItemClick = {
                flowNodeMap[it] = !flowNodeMap[it]!!
                adapterFlowNode.notifyDataSetChanged()
                binding.btnContinue.isEnabled = flowNodeMap.filter {
                    !it.value
                }.isNotEmpty()
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
            btnContinue.isEnabled = false
            cbSelectAll.isChecked = false

            cbSelectAll.setOnCheckedChangeListener { buttonView, isChecked ->
                flowNodeMap.keys.forEach {
                    flowNodeMap[it] = isChecked
                }
                adapterFlowNode.notifyDataSetChanged()
            }

            btnAddNode.setOnClickListener {
                dismiss()
                onConnectNewNode.invoke()
            }

            rvFlowNode.adapter = adapterFlowNode
            val deviceList = vmDevice?.getUserDevices()
             deviceList?.associateWith {
                false
            }?.toMutableMap()?.let {
                 flowNodeMap = it
            }
            adapterFlowNode.submitList(flowNodeMap.entries.toList())

            btnContinue.setOnClickListener {
                dismiss()
                val selectedNodes: List<IoTDevice> = flowNodeMap.filter { it.value }.keys.toList()
                onDevicesSelected.invoke(selectedNodes)
            }
        }
    }
}