package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemLocationManagementBinding
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class AdapterLocation(
    private val onMenuClick: (IoTLocation) -> Unit
) : ListAdapter<IoTLocation, AdapterLocation.LocationViewHolder>(
    object : DiffUtil.ItemCallback<IoTLocation>() {
        override fun areItemsTheSame(oldItem: IoTLocation, newItem: IoTLocation): Boolean {
            return oldItem.uuid!!.contentEquals(newItem.uuid) && oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: IoTLocation, newItem: IoTLocation): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var selectedLocationId: String? = null

    inner class LocationViewHolder(
        private val binding: LayoutItemLocationManagementBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loc: IoTLocation, isSelected: Boolean) {
            binding.apply {
                txtLocLabel.text = loc.label
                cbLocation.isChecked = isSelected
                root.setBackgroundColor(
                    if (isSelected)
                        root.context.getColor(R.color.light_gray)
                    else
                        root.context.getColor(R.color.light_gray_10)
                )

                root.setOnClickListener {
                    if (selectedLocationId != loc.uuid) {
                        selectedLocationId = loc.uuid
                        notifyDataSetChanged()
                    }
                }

                cbLocation.setOnClickListener {
                    if (selectedLocationId != loc.uuid) {
                        selectedLocationId = loc.uuid
                        notifyDataSetChanged()
                    }
                }

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
        val item = getItem(position)
        val isSelected = item.uuid == selectedLocationId
        holder.bind(item, isSelected)
    }

    fun setSelectedLocation(loc: IoTLocation) {
        selectedLocationId = loc.uuid
        notifyDataSetChanged()
    }

    fun setSelectedLocation(uuid: String) {
        selectedLocationId = uuid
        notifyDataSetChanged()
    }

    fun getSelectedLocation(): IoTLocation? {
        return currentList.find { it.uuid == selectedLocationId }
    }
}