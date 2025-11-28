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

/**
 * @file: This adapter is used to display devices
 * @param isAllowedToSelectOneDevice: how many devices can be selected at one time
 * @param onDevicesSelected: triggered when devices are selected successfully
 */
class AdapterDevices(
    private val isAllowedToSelectOneDevice: Boolean = true,
    private val onDevicesSelected: (HashMap<String?, IntArray>) -> Unit
):
ListAdapter<IoTDevice, RecyclerView.ViewHolder>(DIFF) {
    companion object {
        private const val TYPE_SINGLE = 0
        private const val TYPE_GRID = 1
        val DIFF = object : DiffUtil.ItemCallback<IoTDevice>() {
            override fun areItemsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean {
                return oldItem.uuid == newItem.uuid && oldItem.label == newItem.label
            }

            override fun areContentsTheSame(oldItem: IoTDevice, newItem: IoTDevice): Boolean {
                return oldItem == newItem
            }
        }
    }

    //store the selected devices and its selected elements
    private val selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    //store the view for device that has only 1 element
    inner class SingleViewHolder(
        private val binding: LayoutItemDeviceSingleBinding
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                val device = getItem(adapterPosition)

                if (isAllowedToSelectOneDevice) {
                    //clear all selected devices
                    selectedDeviceMap.clear()
                    //set the selected device
                    selectedDeviceMap[device.uuid] = device.elementIds
                } else {
                    //check if the device is already selected
                    if (selectedDeviceMap.keys.contains(device.uuid)) {
                        //if it is already selected, remove it
                        selectedDeviceMap.remove(device.uuid)
                    } else {
                        //if it is not selected yet, set it
                        selectedDeviceMap[device.uuid] = device.elementIds
                    }
                }

                onDevicesSelected.invoke(selectedDeviceMap)
                notifyDataSetChanged()
            }
        }

        fun onBind(device: IoTDevice) {
            binding.apply {
                val location = FlowSdk.locationHandler().get(device.locationId)
                txtLabel.text = device.label
                txtLocation.text = location?.label
                val isSelected = selectedDeviceMap.keys.contains(device.uuid)
                root.isSelected = isSelected

                val backgroundRes = if (isSelected) R.drawable.bg_light_gray_stroke_blue else R.drawable.bg_gray
                root.setBackgroundResource(backgroundRes)
            }
        }
    }

    //store the view for device that has more than 1 element
    inner class GridViewHolder(
        private val binding: LayoutItemDeviceGridBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun onBind(device: IoTDevice) {
            binding.apply {
                val location = FlowSdk.locationHandler().get(device.locationId)
                txtLabel.text = device.label
                txtLocation.text = location?.label

                val adapterElm = AdapterElement(
                    device.uuid,
                    onElementClick = {
                            elmKey ->
                        var selectedElms = selectedDeviceMap[device.uuid]

                        if (isAllowedToSelectOneDevice) {
                            //check if this device is already selected
                            if (selectedDeviceMap.keys.contains(device.uuid)) {
                                //check if the element is already selected
                                if (selectedElms?.contains(elmKey) == true) {
                                    //remove it
                                    selectedDeviceMap[device.uuid] = selectedElms.filter { it != elmKey }.toIntArray()
                                    //remove the device from selected list
                                    if (selectedElms.isEmpty()) {
                                        selectedDeviceMap.remove(device.uuid)
                                    }
                                } else {
                                    //add the elm to selected list
                                    selectedDeviceMap[device.uuid] = selectedElms?.plus(elmKey)?: intArrayOf(elmKey)
                                }
                            } else {
                                selectedDeviceMap.clear()
                                selectedDeviceMap[device.uuid] = intArrayOf(elmKey)
                            }
                        } else {
                            if (selectedDeviceMap.keys.contains(device.uuid)) {
                                if (selectedElms?.contains(elmKey) == true) {
                                    selectedDeviceMap[device.uuid] = selectedElms.filter { it != elmKey }.toIntArray()

                                    if (selectedElms.isEmpty()) {
                                        selectedDeviceMap.remove(device.uuid)
                                    }
                                } else {
                                    selectedDeviceMap[device.uuid] = selectedElms?.plus(elmKey)?: intArrayOf(elmKey)
                                }
                            } else {
                                selectedDeviceMap[device.uuid] = intArrayOf(elmKey)
                            }
                        }
                        notifyDataSetChanged()
                        onDevicesSelected.invoke(selectedDeviceMap)
                    }
                )

                rvElm.adapter = adapterElm
                adapterElm.submitList(device.elementInfos.entries.sortedBy { it.key }.toList())

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

    //class handle element
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
                            txtLabel.text = elmInfo.value.label?: "Nút ${elmInfo.key}"

                            val device = selectedDeviceMap[deviceUuid]
                            val isSelected = device != null && device.contains(elmInfo.key)
                            root.isSelected = isSelected

                            val backgroundRes = if (isSelected) R.drawable.bg_light_gray_stroke_blue else R.drawable.bg_gray
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