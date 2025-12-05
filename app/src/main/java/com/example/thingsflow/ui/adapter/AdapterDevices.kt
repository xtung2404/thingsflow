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
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_GRID
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_SINGLE
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

/**
 * @file: This adapter is used to display devices
 * @param isAllowedToSelectOneDevice: how many devices can be selected at one time
 * @param onDevicesSelected: triggered when devices are selected successfully
 */
class AdapterDevices(
    private val isAllowedToSelectOneDevice: Boolean = true,
    private val onDevicesSelected: (HashMap<String?, IntArray>) -> Unit
) :
    ListAdapter<IoTDevice, RecyclerView.ViewHolder>(DeviceDiffCallback) {
    //store the selected devices and its selected elements
    private val selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()
    fun setSelectedDeviceMap(devMap: HashMap<String?, IntArray>) {
        selectedDeviceMap.clear()
        selectedDeviceMap.putAll(devMap)
        notifyItemRangeChanged(0, itemCount)
    }

    private fun handleSingleDeviceClick(device: IoTDevice, position: Int) {
        selectedDeviceMap.clear()
        selectedDeviceMap[device.uuid] = device.elementIds
        // Thông báo cho toàn bộ danh sách để bỏ chọn item cũ và chọn item mới
        notifyItemRangeChanged(0, itemCount)
    }

    private fun handleMultiDeviceClick(device: IoTDevice, position: Int) {
        if (selectedDeviceMap.containsKey(device.uuid)) {
            selectedDeviceMap.remove(device.uuid)
        } else {
            selectedDeviceMap[device.uuid] = device.elementIds
        }
        notifyItemChanged(position)
    }

    private fun handleElementClick(deviceUuid: String, elementKey: Int) {
        val currentSelections = selectedDeviceMap[deviceUuid]?.toMutableList() ?: mutableListOf()

        if (isAllowedToSelectOneDevice) {
            selectedDeviceMap.clear()
            selectedDeviceMap[deviceUuid] = intArrayOf(elementKey)
        } else {
            if (currentSelections.contains(elementKey)) {
                currentSelections.remove(elementKey)
            } else {
                currentSelections.add(elementKey)
            }

            if (currentSelections.isEmpty()) {
                selectedDeviceMap.remove(deviceUuid)
            } else {
                selectedDeviceMap[deviceUuid] = currentSelections.toIntArray()
            }
        }
        // Thông báo thay đổi cho toàn bộ danh sách để cập nhật UI đúng cách
        notifyItemRangeChanged(0, itemCount)
    }

    //store the view for device that has only 1 element
    inner class SingleViewHolder(
        private val binding: LayoutItemDeviceSingleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                val device = getItem(adapterPosition)

                if (isAllowedToSelectOneDevice) {
                    handleSingleDeviceClick(device, adapterPosition)
                } else {
                    handleMultiDeviceClick(device, adapterPosition)
                }

                onDevicesSelected.invoke(selectedDeviceMap)
            }
        }

        fun onBind(device: IoTDevice) {
            binding.apply {
                txtLabel.text = device.label
                txtLocation.text = FlowSdk.locationHandler().get(device.locationId)?.label ?: ""

                val isSelected = selectedDeviceMap.keys.contains(device.uuid)
                root.isSelected = isSelected

                val backgroundRes =
                    if (isSelected) R.drawable.bg_gray_stroke_emerald else R.drawable.bg_gray
                root.setBackgroundResource(backgroundRes)
            }
        }
    }

    //store the view for device that has more than 1 element
    inner class GridViewHolder(
        private val binding: LayoutItemDeviceGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun onBind(device: IoTDevice) {
            binding.apply {
                txtLabel.text = device.label
                txtLocation.text = FlowSdk.locationHandler().get(device.locationId)?.label ?: ""

                val adapterElm = AdapterElement(
                    device.uuid,
                    onElementClick = { elmKey ->
                        handleElementClick(device.uuid, elmKey)
                        notifyDataSetChanged()
                        onDevicesSelected.invoke(selectedDeviceMap)
                    }
                )

                rvElm.adapter = adapterElm
                adapterElm.submitList(device.elementInfos.entries.sortedBy { it.key }.toList())
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val device = getItem(position)
        return if (device.elementIds.size <= 1) TYPE_SINGLE else TYPE_GRID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SINGLE -> SingleViewHolder(
                LayoutItemDeviceSingleBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )

            else -> GridViewHolder(LayoutItemDeviceGridBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val device = getItem(position)
        when (holder) {
            is SingleViewHolder -> holder.onBind(device)
            is GridViewHolder -> holder.onBind(device)
        }
    }

    //class handle element
    inner class AdapterElement(
        private val deviceUuid: String,
        private val onElementClick: (Int) -> Unit
    ) : ListAdapter<MutableMap.MutableEntry<Int, IoTElementInfo>,
            AdapterElement.ElementViewHolder>(ElementDiffCallback) {
        inner class ElementViewHolder(private val binding: LayoutItemElementBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun onBind(elmInfo: MutableMap.MutableEntry<Int, IoTElementInfo>) {
                binding.apply {
                    txtLabel.text = elmInfo.value.label ?: "Nút ${elmInfo.key}"

                    val device = selectedDeviceMap[deviceUuid]
                    val isSelected = device != null && device.contains(elmInfo.key)
                    root.isSelected = isSelected

                    val backgroundRes =
                        if (isSelected) R.drawable.bg_gray_stroke_emerald else R.drawable.bg_gray
                    root.setBackgroundResource(backgroundRes)

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

private object DeviceDiffCallback : DiffUtil.ItemCallback<IoTDevice>() {
    override fun areItemsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean =
        oldItem.uuid == newItem.uuid

    override fun areContentsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean =
        oldItem == newItem
}

private object ElementDiffCallback :
    DiffUtil.ItemCallback<MutableMap.MutableEntry<Int, IoTElementInfo>>() {
    override fun areItemsTheSame(
        oldItem: MutableMap.MutableEntry<Int, IoTElementInfo>,
        newItem: MutableMap.MutableEntry<Int, IoTElementInfo>
    ): Boolean = oldItem.key == newItem.key

    override fun areContentsTheSame(
        oldItem: MutableMap.MutableEntry<Int, IoTElementInfo>,
        newItem: MutableMap.MutableEntry<Int, IoTElementInfo>
    ): Boolean = oldItem.value == newItem.value
}