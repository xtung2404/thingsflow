package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemDeviceGridBinding
import com.example.thingsflow.databinding.LayoutItemDeviceSingleBinding
import com.example.thingsflow.databinding.LayoutItemElementBinding
import com.example.thingsflow.databinding.LayoutItemFlowNodeBinding
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import java.util.concurrent.Flow

class AdapterFlowNode(
    private val onItemClick: (IoTDevice) -> Unit
):
ListAdapter<MutableMap.MutableEntry<IoTDevice, Boolean>, AdapterFlowNode.FlowNodeViewHolder>(
    object : DiffUtil.ItemCallback<MutableMap.MutableEntry<IoTDevice, Boolean>>() {
        override fun areItemsTheSame(
            oldItem: MutableMap.MutableEntry<IoTDevice, Boolean>,
            newItem: MutableMap.MutableEntry<IoTDevice, Boolean>
        ): Boolean {
            return oldItem.key.uuid == newItem.key.uuid && oldItem.key.label == newItem.key.label && oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: MutableMap.MutableEntry<IoTDevice, Boolean>,
            newItem: MutableMap.MutableEntry<IoTDevice, Boolean>
        ): Boolean {
            return oldItem.key.uuid == newItem.key.uuid && oldItem.key.label == newItem.key.label && oldItem.value == newItem.value
        }
    }
) {
    inner class FlowNodeViewHolder(
        private val binding: LayoutItemFlowNodeBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(node: MutableMap.MutableEntry<IoTDevice, Boolean>) {
            binding.apply {
                val location = FlowSdk.locationHandler().get(node.key.locationId)
                txtLabel.text = node.key.label
                txtLocation.text = location?.label
                if(node.value) {
                    imgCheck.visibility = View.VISIBLE
                } else {
                    imgCheck.visibility = View.GONE
                }
                root.setOnClickListener {
                    onItemClick.invoke(node.key)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowNodeViewHolder {
        val inflater = LayoutItemFlowNodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlowNodeViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FlowNodeViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

}