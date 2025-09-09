package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemDiscoveredDeviceBinding
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

class AdapterDiscoveredZigbeeDevices(
    private val onItemSelected: (IoTPairedZigbeeDevice) -> Unit
):
ListAdapter<IoTPairedZigbeeDevice, AdapterDiscoveredZigbeeDevices.DiscoveredZigbeeDevicesViewHolder>(
    object : DiffUtil.ItemCallback<IoTPairedZigbeeDevice>() {
        override fun areItemsTheSame(
            oldItem: IoTPairedZigbeeDevice,
            newItem: IoTPairedZigbeeDevice
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: IoTPairedZigbeeDevice,
            newItem: IoTPairedZigbeeDevice
        ): Boolean {
            return false
        }

    }
) {
    inner class DiscoveredZigbeeDevicesViewHolder(
        private val binding: LayoutItemDiscoveredDeviceBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(device: IoTPairedZigbeeDevice) {
            binding.apply {
                txtLabel.text = device.ioTProductModel.name
                root.setOnClickListener {
                    onItemSelected.invoke(device)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveredZigbeeDevicesViewHolder {
        val inflater = LayoutItemDiscoveredDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoveredZigbeeDevicesViewHolder(inflater)
    }

    override fun onBindViewHolder(
        holder: DiscoveredZigbeeDevicesViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }
}