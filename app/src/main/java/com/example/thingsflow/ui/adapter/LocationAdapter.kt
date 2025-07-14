package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemLocationManagementBinding
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class LocationAdapter(
    private val onMenuClick: (IoTLocation) -> Unit
): ListAdapter<IoTLocation, LocationAdapter.LocationViewHolder>(
    object : DiffUtil.ItemCallback<IoTLocation>() {
        override fun areItemsTheSame(oldItem: IoTLocation, newItem: IoTLocation): Boolean {
            return oldItem.uuid!!.contentEquals(newItem.uuid) && oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: IoTLocation, newItem: IoTLocation): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class LocationViewHolder(
        private val binding: LayoutItemLocationManagementBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(loc: IoTLocation) {
            binding.apply {
                txtLocLabel.text = loc.label
                btnOption.setOnClickListener {
                    onMenuClick.invoke(loc)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val inflater = LayoutItemLocationManagementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}