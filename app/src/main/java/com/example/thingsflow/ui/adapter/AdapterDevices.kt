package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemDeviceGridBinding
import com.example.thingsflow.databinding.LayoutItemDeviceSingleBinding
import com.example.thingsflow.databinding.LayoutItemElementBinding
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import java.util.concurrent.Flow

class AdapterDevices(
    private val onDeviceSelected: (String, IntArray) -> Unit
):
ListAdapter<IoTDevice, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<IoTDevice>() {
        override fun areItemsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean {
            return oldItem.uuid == newItem.uuid && oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean {
            return oldItem == newItem
        }
    }
) {
    companion object {
        private const val TYPE_SINGLE = 0
        private const val TYPE_GRID = 1
    }

    private var selectedDeviceUuid: String? = null
    private var selectedElementKeys: MutableSet<Int> = mutableSetOf()
    inner class SingleViewHolder(
        private val binding: LayoutItemDeviceSingleBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(device: IoTDevice) {
            binding.apply {
                val location = FlowSdk.locationHandler().get(device.locationId)
                txtLabel.text = device.label
                txtLocation.text = location?.label
                root.isSelected = (device.uuid == selectedDeviceUuid)

                if (device.uuid == selectedDeviceUuid) {
                    root.setBackgroundResource(R.drawable.bg_light_gray_stroke_blue)
                } else {
                    root.setBackgroundColor(root.context.getColor(R.color.light_gray))
                }
                root.setOnClickListener {
                    selectedDeviceUuid = device.uuid
                    selectedElementKeys = device.elementIds.toMutableSet()
                    onDeviceSelected.invoke(device.uuid, device.elementIds)
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class GridViewHolder(
        private val binding: LayoutItemDeviceGridBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(device: IoTDevice) {
            binding.apply {
                val location = FlowSdk.locationHandler().get(device.locationId)
                val adapterElm = AdapterElement(
                    device.uuid,
                    onElementClick = {
                            elmKey ->
                        // callback khi chọn element
                        if (device.uuid != selectedDeviceUuid) {
                            // chọn device mới -> reset
                            selectedDeviceUuid = device.uuid
                            selectedElementKeys = mutableSetOf()
                        }
                        // toggle chọn element
                        if (!selectedElementKeys.contains(elmKey)) {
                            selectedElementKeys.add(elmKey)
                        }

                        onDeviceSelected.invoke(
                            device.uuid,
                            selectedElementKeys.toIntArray()
                        )
                        notifyDataSetChanged()
                    }
                )
                rvElm.adapter = adapterElm
                adapterElm.submitList(device.elementInfos.entries.toList())
                txtLabel.text = device.label
                txtLocation.text = location?.label
                root.setOnClickListener {

                }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        val device = getItem(position)
        return if (device.elementIds.size <= 1) TYPE_SINGLE else TYPE_GRID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            TYPE_SINGLE -> {
                val inflater = LayoutItemDeviceSingleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SingleViewHolder(inflater)
            }
            else -> {
                val inflater = LayoutItemDeviceGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return GridViewHolder(inflater)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val device = getItem(position)
        when(holder) {
            is SingleViewHolder -> holder.onBind(device)
            is GridViewHolder -> holder.onBind(device)
        }
    }

    inner class AdapterElement(
        private val deviceUuid: String,
        private val onElementClick: (Int) -> Unit
    ):
        ListAdapter<MutableMap.MutableEntry<Int, IoTElementInfo>, AdapterElement.ElementViewHolder>(object: DiffUtil.ItemCallback<MutableMap.MutableEntry<Int, IoTElementInfo>>() {
            override fun areItemsTheSame(
                oldItem: MutableMap.MutableEntry<Int, IoTElementInfo>,
                newItem: MutableMap.MutableEntry<Int, IoTElementInfo>
            ): Boolean {
                return oldItem.key == newItem.key && oldItem.value.label == newItem.value.label
            }

            override fun areContentsTheSame(
                oldItem: MutableMap.MutableEntry<Int, IoTElementInfo>,
                newItem: MutableMap.MutableEntry<Int, IoTElementInfo>
            ): Boolean {
                return oldItem.key == newItem.key && oldItem.value.label == newItem.value.label
            }
        }
        ) {
            inner class ElementViewHolder(private val binding: LayoutItemElementBinding)
                : RecyclerView.ViewHolder(binding.root) {
                    fun onBind(elmInfo: MutableMap.MutableEntry<Int, IoTElementInfo>) {
                        binding.apply {
                            txtLabel.text = elmInfo.value.label
                            val isSelected = (selectedDeviceUuid == deviceUuid && selectedElementKeys.contains(elmInfo.key))
                            root.isSelected = isSelected
                            if (isSelected) {
                                root.setBackgroundResource(R.drawable.bg_light_gray_stroke_blue)
                            } else {
                                root.setBackgroundColor(root.context.getColor(R.color.light_gray))
                            }
                            root.setOnClickListener {
                                onElementClick.invoke(elmInfo.key)
                            }
                        }
                    }
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementViewHolder {
            val inflater = LayoutItemElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ElementViewHolder(inflater)
        }

        override fun onBindViewHolder(holder: ElementViewHolder, position: Int) {
            holder.onBind(getItem(position))
        }
    }
}