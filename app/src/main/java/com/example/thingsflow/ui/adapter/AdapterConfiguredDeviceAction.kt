package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutItemDeviceActionGridBinding
import com.example.thingsflow.databinding.LayoutItemDeviceActionSingleBinding
import com.example.thingsflow.databinding.LayoutItemElementCmdBinding
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_GRID
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_SINGLE
import com.example.thingsflow.utils.getCmdLabel
import rogo.iot.module.flowcommon.value.FControlValue

class AdapterConfiguredDeviceAction
    : ListAdapter<Map.Entry<String?, Array<FControlValue>>, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<Map.Entry<String?, Array<FControlValue>>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<String?, Array<FControlValue>>,
            newItem: Map.Entry<String?, Array<FControlValue>>
        ): Boolean {
            return oldItem.key.contentEquals(newItem.key) && oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<String?, Array<FControlValue>>,
            newItem: Map.Entry<String?, Array<FControlValue>>
        ): Boolean {
            return oldItem.key.contentEquals(newItem.key) && oldItem.value == newItem.value
        }
    }
) {
    inner class SingleViewHolder(private val binding: LayoutItemDeviceActionSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(deviceActionEntry: Map.Entry<String?, Array<FControlValue>>) {
            binding.apply {
                val devId = deviceActionEntry.key
                val configuredActions = deviceActionEntry.value

                // get device info based on uuid
                val device = FlowSdk.deviceHandler().get(devId)
                device?.let { dev ->
                    txtLabel.text = dev.label
                    val location = FlowSdk.locationHandler().get(dev.locationId)
                    txtLocation.text = location?.label ?: ""
                }

                //cause this device has only 1 element, so there will be only 1 command that can be setted
                val action = configuredActions.firstOrNull()
                action.let { act ->
                    txtAction.text = getCmdLabel(
                        root.context,
                        act?.attrValue?.get(0), // Lệnh
                        act?.attrValue?.get(1) // Giá trị của lệnh (nếu có)
                    )
                }
            }
        }
    }

    inner class GridViewHolder(private val binding: LayoutItemDeviceActionGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Map.Entry<String?, Array<FControlValue>>) {
            binding.apply {
                val device = FlowSdk.deviceHandler().get(item.key)
                device?.let { dev ->
                    txtLabel.text = dev.label
                    val location = FlowSdk.locationHandler().get(dev.locationId)
                    txtLocation.text = location?.label ?: ""

                    val adapterElementCmd = AdapterElementCmd(dev.uuid)
                    rvElms.adapter = adapterElementCmd
                    adapterElementCmd.submitList(item.value.toList())
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val device = FlowSdk.deviceHandler().userDevices.find {
            it.uuid.contentEquals(getItem(position).key)
        }
        val type = if (device?.elementIds?.size!! <= 1) TYPE_SINGLE else TYPE_GRID
        return type
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_SINGLE -> {
                val inflater = LayoutItemDeviceActionSingleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SingleViewHolder(inflater)
            }

            else -> {
                val inflater = LayoutItemDeviceActionGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return GridViewHolder(inflater)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val device = getItem(position)
        when (holder) {
            is SingleViewHolder -> holder.onBind(device)
            is GridViewHolder -> holder.onBind(device)
        }
    }

    inner class AdapterElementCmd(
        private val devId: String?= null
    ) :
        ListAdapter<FControlValue, AdapterElementCmd.ElementCmdViewHolder>(
            object : DiffUtil.ItemCallback<FControlValue>() {
                override fun areItemsTheSame(
                    oldItem: FControlValue,
                    newItem: FControlValue
                ): Boolean {
                    return oldItem.elm == newItem.elm && oldItem.attrValue.contentEquals(newItem.attrValue)
                }

                override fun areContentsTheSame(
                    oldItem: FControlValue,
                    newItem: FControlValue
                ): Boolean {
                    return oldItem.elm == newItem.elm && oldItem.attrValue.contentEquals(newItem.attrValue)
                }
            }
        ) {
        inner class ElementCmdViewHolder(private val binding: LayoutItemElementCmdBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun onBind(elmCmd: FControlValue) {
                binding.apply {
                    val device = FlowSdk.deviceHandler().get(devId)
                    device?.let { dev->
                        val elmInfo = dev.elementInfos?.toList()?.find { it.first == elmCmd.elm }

                        txtElm.text = elmInfo?.second?.label ?: "Nút ${elmInfo?.first}"
                        txtDesc.text = getCmdLabel(root.context, elmCmd.attrValue[0], elmCmd.attrValue[1])
                    }
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ElementCmdViewHolder {
            val inflater = LayoutItemElementCmdBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ElementCmdViewHolder(inflater)
        }

        override fun onBindViewHolder(holder: ElementCmdViewHolder, position: Int) {
            holder.onBind(getItem(position))
        }
    }
}