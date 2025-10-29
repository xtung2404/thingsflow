package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemDeviceGatewayBinding
import com.example.thingsflow.databinding.LayoutItemDeviceGridBinding
import com.example.thingsflow.databinding.LayoutItemDeviceSingleBinding
import com.example.thingsflow.databinding.LayoutItemElementBinding
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import java.util.concurrent.Flow

class AdapterGateway(
    private val onDeviceSelected: (String, IntArray) -> Unit
):
ListAdapter<IoTDevice, AdapterGateway.GatewayViewHolder>(
    object : DiffUtil.ItemCallback<IoTDevice>() {
        override fun areItemsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean {
            return oldItem.uuid == newItem.uuid && oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var selectedDeviceUuid: String? = null
    private var selectedElementKeys: MutableSet<Int> = mutableSetOf()
    inner class GatewayViewHolder(
        private val binding: LayoutItemDeviceGatewayBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(device: IoTDevice) {
            binding.apply {
                val group = FlowSdk.groupHandler().get(device.groupId)
                txtLabel.text = device.label
                txtDesc.text = if (group == null) root.context.getString(R.string.device_has_not_been_assigned_to_group) else group.label
                root.isSelected = (device.uuid == selectedDeviceUuid)
                if (device.uuid == selectedDeviceUuid) {
                    root.setBackgroundResource(R.drawable.bg_gray_stroke_emerald)
                    cb.isChecked = true
                } else {
                    root.setBackgroundColor(root.context.getColor(R.color.light_gray))
                    cb.isChecked = false
                }
                root.setOnClickListener {
                    selectedDeviceUuid = device.uuid
                    selectedElementKeys = device.elementIds.toMutableSet()
                    cb.isChecked = true
                    ILogR.D("AdapterGateway", "onBind: ${device.uuid}")
                    onDeviceSelected.invoke(device.uuid, device.elementIds)
                    notifyDataSetChanged()
                }

                cb.setOnCheckedChangeListener { _, isChecked ->
                    selectedDeviceUuid = device.uuid
                    selectedElementKeys = device.elementIds.toMutableSet()
                    cb.isChecked = true
                    onDeviceSelected.invoke(device.uuid, device.elementIds)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: GatewayViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GatewayViewHolder {
        val inflater = LayoutItemDeviceGatewayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GatewayViewHolder(inflater)
    }
}