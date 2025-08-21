package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.databinding.DialogDeviceListBinding
import com.example.thingsflow.databinding.DialogFlowNodeListBinding
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterFlowNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogFlowNodeList(
    context: Context,
    private val viewModelOwner: ViewModelStoreOwner,
    private val onConnectNewNode: () -> Unit
): DialogBase<DialogFlowNodeListBinding>(
    context,
    R.layout.dialog_flow_node_list
)  {
    private val TAG = "DialogFlowNodeList"
    private var flowNodeMap: MutableMap<IoTDevice, Boolean> = mutableMapOf()
    private val adapterFlowNode: AdapterFlowNode by lazy {
        AdapterFlowNode(
            onItemClick = {
                flowNodeMap[it] = !flowNodeMap[it]!!
                adapterFlowNode.notifyDataSetChanged()
            }
        )
    }

    override fun setupView(binding: DialogFlowNodeListBinding) {
        binding.apply {
            btnBack.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            btnAddNode.setOnClickListener {
                dismiss()
                onConnectNewNode.invoke()
            }

            rvFlowNode.adapter = adapterFlowNode
            val deviceList = SmartSdk.deviceHandler().all
            flowNodeMap = deviceList.associateWith {
                false
            }.toMutableMap()
            adapterFlowNode.submitList(flowNodeMap.entries.toList())
        }
    }
}