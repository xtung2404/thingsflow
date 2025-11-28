package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.View
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
import com.example.thingsflow.databinding.LayoutItemSelectedDeviceBinding
import rogo.iot.module.base.ILogR
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import java.util.concurrent.Flow

class AdapterSelectedDevice() :
    ListAdapter<Map.Entry<String?, IntArray>, AdapterSelectedDevice.SelectedDeviceViewHolder>(
        object : DiffUtil.ItemCallback<Map.Entry<String?, IntArray>>() {
            override fun areItemsTheSame(
                oldItem: Map.Entry<String?, IntArray>,
                newItem: Map.Entry<String?, IntArray>
            ): Boolean {
                return oldItem.key == newItem.key && oldItem.value == newItem.value
            }

            override fun areContentsTheSame(
                oldItem: Map.Entry<String?, IntArray>,
                newItem: Map.Entry<String?, IntArray>
            ): Boolean {
                return oldItem.key == newItem.key && oldItem.value == newItem.value
            }
        }
    ) {
    private val adapterSelectedElement: AdapterSelectedElement by lazy {
        AdapterSelectedElement()
    }

    inner class SelectedDeviceViewHolder(
        private val binding: LayoutItemSelectedDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(selectedDevice: Map.Entry<String?, IntArray>) {
            binding.apply {
                val device = FlowSdk.deviceHandler().get(selectedDevice.key)
                val selectedElements = hashMapOf<Int, IoTElementInfo>()
                device?.let {
                    txtLabel.text = device.label
                    val location = FlowSdk.locationHandler().get(device.locationId)
                    txtLocation.text = location?.label
                    if (device.elementIds.size == 1) {
                        rvElm.visibility = View.GONE
                    } else {
                        rvElm.visibility = View.VISIBLE
                        rvElm.adapter = adapterSelectedElement
                        device.elementInfos.forEach {
                            if (selectedDevice.value.contains(it.key)) {
                                selectedElements[it.key] = it.value
                            }
                        }
                        adapterSelectedElement.submitList(
                            selectedElements.entries.toList()
                        )
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: SelectedDeviceViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedDeviceViewHolder {
        val inflater = LayoutItemSelectedDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectedDeviceViewHolder(inflater)
    }

    inner class AdapterSelectedElement() : ListAdapter<
            Map.Entry<Int, IoTElementInfo>,
            AdapterSelectedElement.SelectedElementViewHolder
            >(object : DiffUtil.ItemCallback<Map.Entry<Int, IoTElementInfo>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<Int, IoTElementInfo>,
            newItem: Map.Entry<Int, IoTElementInfo>
        ): Boolean {
            return oldItem.key == newItem.key && oldItem.value.label == newItem.value.label
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<Int, IoTElementInfo>,
            newItem: Map.Entry<Int, IoTElementInfo>
        ): Boolean {
            return oldItem.key == newItem.key && oldItem.value.label == newItem.value.label
        }
    }
            ) {
        inner class SelectedElementViewHolder(
            private val binding: LayoutItemElementBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(element: Map.Entry<Int, IoTElementInfo>) {
                binding.apply {
                    txtLabel.text = element.value.label ?: "Nút ${element.key}"
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SelectedElementViewHolder {
            val inflater = LayoutItemElementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SelectedElementViewHolder(inflater)
        }

        override fun onBindViewHolder(
            holder: SelectedElementViewHolder,
            position: Int
        ) {
            holder.bind(getItem(position))
        }
    }
}