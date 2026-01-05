package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemDiscoveredDeviceBinding
import rogo.iot.module.base.entity.IoTModelSmartConfig
import rogo.iot.module.rogocore.sdk.SmartSdk

/**
 * adapter for showing discovered devices(gateway)
 */
class AdapterDiscoveredDevices(
    private val onItemSelected: (IoTModelSmartConfig) -> Unit
):
ListAdapter<IoTModelSmartConfig, AdapterDiscoveredDevices.DiscoveredDevicesViewHolder>(
    object : DiffUtil.ItemCallback<IoTModelSmartConfig>() {
        override fun areItemsTheSame(
            oldItem: IoTModelSmartConfig,
            newItem: IoTModelSmartConfig
        ): Boolean {
            return oldItem.mac == newItem.mac && oldItem.label.contentEquals(newItem.label)
        }

        override fun areContentsTheSame(
            oldItem: IoTModelSmartConfig,
            newItem: IoTModelSmartConfig
        ): Boolean {
            return oldItem.mac == newItem.mac
        }
    }
) {
    inner class DiscoveredDevicesViewHolder(
        private val binding: LayoutItemDiscoveredDeviceBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(device: IoTModelSmartConfig) {
            binding.apply {
                txtLabel.text = SmartSdk.getProductModel(device.productId).name
                imgCheck.visibility = View.GONE
                root.setOnClickListener {
                    onItemSelected.invoke(device)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveredDevicesViewHolder {
        val inflater = LayoutItemDiscoveredDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoveredDevicesViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: DiscoveredDevicesViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}