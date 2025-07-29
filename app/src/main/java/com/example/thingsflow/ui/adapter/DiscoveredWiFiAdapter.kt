package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemDiscoveredWifiBinding
import rogo.iot.module.platform.define.IoTWifiAuthType
import rogo.iot.module.platform.entity.IoTWifiInfo

class DiscoveredWiFiAdapter(
    private val onWiFiSelected: (IoTWifiInfo) -> Unit
):
ListAdapter<IoTWifiInfo, DiscoveredWiFiAdapter.DiscoveredWiFiViewHolder>(
    object : DiffUtil.ItemCallback<IoTWifiInfo>() {
        override fun areItemsTheSame(oldItem: IoTWifiInfo, newItem: IoTWifiInfo): Boolean {
            return oldItem.ssid == newItem.ssid
        }

        override fun areContentsTheSame(oldItem: IoTWifiInfo, newItem: IoTWifiInfo): Boolean {
            return false
        }
    }
) {
    inner class DiscoveredWiFiViewHolder(
        private val binding: LayoutItemDiscoveredWifiBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(ioTWifiInfo: IoTWifiInfo) {
            binding.apply {
                txtLabel.text = ioTWifiInfo.ssid
                root.setOnClickListener {
                    onWiFiSelected.invoke(ioTWifiInfo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveredWiFiViewHolder {
        val inflater = LayoutItemDiscoveredWifiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoveredWiFiViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: DiscoveredWiFiViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }


}