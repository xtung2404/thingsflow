package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemDiscoveredDeviceBinding
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

/**
 * adapter for showing discovered Zigbee devices
 * @param isAllowToSelect if this view allow user to choose an item
 */
class AdapterDiscoveredZigbeeDevices(
    private val isAllowToSelect: Boolean = false,
    private val onItemSelected: (IoTPairedZigbeeDevice) -> Unit
):
ListAdapter<Map.Entry<IoTPairedZigbeeDevice, Boolean>, AdapterDiscoveredZigbeeDevices.DiscoveredZigbeeDevicesViewHolder>(
    object : DiffUtil.ItemCallback<Map.Entry<IoTPairedZigbeeDevice, Boolean>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<IoTPairedZigbeeDevice, Boolean>,
            newItem: Map.Entry<IoTPairedZigbeeDevice, Boolean>
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<IoTPairedZigbeeDevice, Boolean>,
            newItem: Map.Entry<IoTPairedZigbeeDevice, Boolean>
        ): Boolean {
            return false
        }
    }
) {
    private val selectedDeviceSet: MutableSet<IoTPairedZigbeeDevice> = mutableSetOf()
    inner class DiscoveredZigbeeDevicesViewHolder(
        private val binding: LayoutItemDiscoveredDeviceBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(device: Map.Entry<IoTPairedZigbeeDevice, Boolean>) {
            binding.apply {
                txtLabel.text = device.key.ioTProductModel.name
                imgCheck.visibility = View.GONE
                if (isAllowToSelect) {
                    if (selectedDeviceSet.contains(device.key)) {
                        root.setBackgroundResource(R.drawable.bg_gray_stroke_emerald)
                        imgCheck.visibility = View.VISIBLE
                    } else {
                        root.setBackgroundResource(R.drawable.bg_gray)
                        imgCheck.visibility = View.INVISIBLE
                    }
                    root.setOnClickListener {
                        if (selectedDeviceSet.contains(device.key)) {
                            selectedDeviceSet.remove(device.key)
                        } else {
                            selectedDeviceSet.add(device.key)
                        }
                        notifyItemChanged(position)
                        onItemSelected.invoke(device.key)
                    }
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

    fun getSelectedDevices(): List<IoTPairedZigbeeDevice> = selectedDeviceSet.toList()
}