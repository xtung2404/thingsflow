package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutItemSetControlActionElmBinding
import com.example.thingsflow.databinding.LayoutItemSetControlActionGridBinding
import com.example.thingsflow.databinding.LayoutItemSetControlActionSingleBinding
import com.example.thingsflow.module.define.TFElementCmd
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_GRID
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_SINGLE
import com.example.thingsflow.utils.show
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.platform.entity.IoTElementInfo

class AdapterConfigControlCommand(
    context: Context,
    private  val action: Int,
) : ListAdapter<Map.Entry<String?, IntArray>, RecyclerView.ViewHolder>(
object : DiffUtil.ItemCallback<Map.Entry<String?, IntArray>>() {
    override fun areItemsTheSame(
        oldItem: Map.Entry<String?, IntArray>,
        newItem: Map.Entry<String?, IntArray>
    ): Boolean {
        return oldItem.key.contentEquals(newItem.key) && oldItem.value.contentEquals(newItem.value)
    }

    override fun areContentsTheSame(
        oldItem: Map.Entry<String?, IntArray>,
        newItem: Map.Entry<String?, IntArray>
    ): Boolean {
        return oldItem.key.contentEquals(newItem.key) && oldItem.value.contentEquals(newItem.value)
    }

}
) {
    private val TAG = "AdapterConfigControlCommand"
    private var deviceActionMap: HashMap<String?, ArrayList<TFElementCmd>> = hashMapOf()

    fun getDeviceActionMap(): HashMap<String?, ArrayList<TFElementCmd>> {
        return deviceActionMap
    }
    private lateinit var adapterControlElement: AdapterControlElement
    inner class SingleViewHolder(private val binding: LayoutItemSetControlActionSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(deviceEntry: Map.Entry<String?, IntArray>) {
            binding.apply {
                val device = FlowSdk.deviceHandler().get(deviceEntry.key)
                device?.let {
                    txtLabel.text = it.label
                    val location = FlowSdk.locationHandler().get(it.locationId)
                    location?.let { loc ->
                        txtLocation.text = loc.label
                    }

                    lnControlOnOff.initView(onOnOffSelected = { newActionValue ->
                        val elm = device.elementIds.first()
                        var commands = deviceActionMap[device.uuid]
                        val newCmd = TFElementCmd(elm, newActionValue)
                        if (commands != null) {
                            val cmd = commands.firstOrNull {
                                it.elmId == elm
                            }
                            if (cmd != null) {
                                if (cmd.cmd.contentEquals(newActionValue)) {
                                    commands.remove(cmd)
                                } else {
                                    commands.remove(cmd)
                                    commands.add(newCmd)
                                }
                            } else {
                                commands.add(newCmd)
                            }
                        } else {
                            commands = arrayListOf<TFElementCmd>(newCmd)
                        }
                        if (commands.isNotEmpty()) {
                            deviceActionMap[device.uuid] = commands
                        } else {
                            deviceActionMap.remove(device.uuid)
                        }
                        cbConfiged.isChecked = deviceActionMap[device.uuid]?.isEmpty() == false
                        ILogR.D(TAG, "onOnOffSelected:actionSize ", deviceActionMap.size)
                        deviceActionMap.entries.forEach { entry ->
                            ILogR.D(TAG, "onOnOffSelected:device ", device.uuid, entry.value.size)
                            entry.value.forEach { value ->
                                ILogR.D(TAG, "onOnOffSelected:elmAction ", value.elmId, value.cmd.contentToString())
                            }
                        }
                        lnControlOnOff.updateButtonState(deviceActionMap[device.uuid]?.find { it == newCmd }?.cmd)
                    })
                }
            }
        }
    }

    inner class GridViewHolder(private val binding: LayoutItemSetControlActionGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(deviceEntry: Map.Entry<String?, IntArray>) {
            binding.apply {
                adapterControlElement = AdapterControlElement(
                    deviceEntry.key,
                    action,
                    onCmdSelected = {
                        cbConfiged.isChecked = deviceActionMap[deviceEntry.key]?.isEmpty() == false
                    }
                )

                val device = FlowSdk.deviceHandler().get(deviceEntry.key)
                val selectedElmsInfo = hashMapOf<Int, IoTElementInfo>()
                device?.let { dev ->
                    txtLabel.text = dev.label
                    val location = FlowSdk.locationHandler().get(dev.locationId)
                    location?.let { loc ->
                        txtLocation.text = loc.label
                    }
                    deviceEntry.value.forEach { elm->
                        selectedElmsInfo[elm] = dev.elementInfos?.toList()?.find { it.first == elm }?.second!!
                    }

                    rvElms.adapter = adapterControlElement
                    adapterControlElement.submitList(selectedElmsInfo.entries.toList())
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
                val inflater = LayoutItemSetControlActionSingleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SingleViewHolder(inflater)
            }

            else -> {
                val inflater = LayoutItemSetControlActionGridBinding.inflate(
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
        when (holder) {
            is SingleViewHolder -> holder.onBind(device)
            is GridViewHolder -> holder.onBind(device)
        }
    }

    inner class AdapterControlElement(
        private val devId: String?= null,
        private val action: Int,
        private val onCmdSelected: () -> Unit
    ) :
        ListAdapter<MutableMap.MutableEntry<Int, IoTElementInfo>, AdapterControlElement.ControlElementViewHolder>(
            object : DiffUtil.ItemCallback<MutableMap.MutableEntry<Int, IoTElementInfo>>() {
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
        inner class ControlElementViewHolder(private val binding: LayoutItemSetControlActionElmBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun onBind(elmInfo: MutableMap.MutableEntry<Int, IoTElementInfo>) {
                binding.apply {
                    txtLabel.text = elmInfo.value.label?: "Nuts ${elmInfo.key}"

                    when(action) {
                        IoTAttribute.ACT_ONOFF -> lnControlOnOff.show()
                    }

                    lnControlOnOff.initView(onOnOffSelected = { newActionValue ->
                        val device = FlowSdk.deviceHandler().get(devId)
                        device?.let {
                            var commands = deviceActionMap[device.uuid]
                            val newCmd = TFElementCmd(elmInfo.key, newActionValue)
                            if (commands != null) {
                                val cmd = commands.firstOrNull {
                                    it.elmId == elmInfo.key
                                }
                                if (cmd != null) {
                                    if (cmd.cmd.contentEquals(newActionValue)) {
                                        commands.remove(cmd)
                                    } else {
                                        commands.remove(cmd)
                                        commands.add(newCmd)
                                    }
                                } else {
                                    commands.add(newCmd)
                                }
                            } else {
                                commands = arrayListOf<TFElementCmd>(newCmd)
                            }
                            if (commands.isNotEmpty()) {
                                deviceActionMap[device.uuid] = commands
                            } else {
                                deviceActionMap.remove(device.uuid)
                            }

                            ILogR.D(TAG, "onOnOffSelected:actionSize ", deviceActionMap.size)
                            deviceActionMap.entries.forEach { entry ->
                                ILogR.D(TAG, "onOnOffSelected:device ", device.uuid, entry.value.size)
                                entry.value.forEach { value ->
                                    ILogR.D(TAG, "onOnOffSelected:elmAction ", value.elmId, value.cmd.contentToString())
                                }
                            }

                            onCmdSelected.invoke()
                            lnControlOnOff.updateButtonState(deviceActionMap[device.uuid]?.find { it == newCmd }?.cmd)
                        }
                    })
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControlElementViewHolder {
            val inflater = LayoutItemSetControlActionElmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ControlElementViewHolder(inflater)
        }

        override fun onBindViewHolder(holder: ControlElementViewHolder, position: Int) {
            holder.onBind(getItem(position))
        }
    }
}