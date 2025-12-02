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
import com.example.thingsflow.module.define.TFElementCmd
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_GRID
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_SINGLE
import com.example.thingsflow.utils.getCmdLabel

class AdapterConfigedDeviceAction
    : ListAdapter<Map.Entry<String?, ArrayList<TFElementCmd>>, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<Map.Entry<String?, ArrayList<TFElementCmd>>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<String?, ArrayList<TFElementCmd>>,
            newItem: Map.Entry<String?, ArrayList<TFElementCmd>>
        ): Boolean {
            return oldItem.key.contentEquals(newItem.key) && oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<String?, ArrayList<TFElementCmd>>,
            newItem: Map.Entry<String?, ArrayList<TFElementCmd>>
        ): Boolean {
            return oldItem.key.contentEquals(newItem.key) && oldItem.value == newItem.value
        }
    }
) {
    inner class SingleViewHolder(private val binding: LayoutItemDeviceActionSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(deviceActionEntry: Map.Entry<String?, ArrayList<TFElementCmd>>) {
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
                        act?.cmd?.get(0), // Lệnh
                        act?.cmd?.get(1) // Giá trị của lệnh (nếu có)
                    )
                }
            }
        }
    }

    inner class GridViewHolder(private val binding: LayoutItemDeviceActionGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: Map.Entry<String?, ArrayList<TFElementCmd>>) {
            binding.apply {
                val device = FlowSdk.deviceHandler().get(item.key)
                device?.let { dev ->
                    txtLabel.text = dev.label
                    val location = FlowSdk.locationHandler().get(dev.locationId)
                    txtLocation.text = location?.label ?: ""

                    val adapterElementCmd = AdapterElementCmd(dev.uuid)
                    rvElms.adapter = adapterElementCmd
                    adapterElementCmd.submitList(item.value)
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
        ListAdapter<TFElementCmd, AdapterElementCmd.ElementCmdViewHolder>(
            object : DiffUtil.ItemCallback<TFElementCmd>() {
                override fun areItemsTheSame(
                    oldItem: TFElementCmd,
                    newItem: TFElementCmd
                ): Boolean {
                    return oldItem.elmId == newItem.elmId && oldItem.cmd.contentEquals(newItem.cmd)
                }

                override fun areContentsTheSame(
                    oldItem: TFElementCmd,
                    newItem: TFElementCmd
                ): Boolean {
                    return oldItem.elmId == newItem.elmId && oldItem.cmd.contentEquals(newItem.cmd)
                }
            }
        ) {
        inner class ElementCmdViewHolder(private val binding: LayoutItemElementCmdBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun onBind(elmCmd: TFElementCmd) {
                binding.apply {
                    val device = FlowSdk.deviceHandler().get(devId)
                    device?.let { dev->
                        val elmInfo = dev.elementInfos?.toList()?.find { it.first == elmCmd.elmId }

                        txtElm.text = elmInfo?.second?.label ?: "Nuts ${elmInfo?.first}"
                        txtDesc.text = getCmdLabel(root.context, elmCmd.cmd[0], elmCmd.cmd[1])
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