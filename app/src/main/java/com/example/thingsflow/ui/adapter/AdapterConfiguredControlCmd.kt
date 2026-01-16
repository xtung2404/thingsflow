package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemSetControlActionElmBinding
import com.example.thingsflow.databinding.LayoutItemSetControlActionGridBinding
import com.example.thingsflow.databinding.LayoutItemSetControlActionSingleBinding
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_GRID
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_SINGLE
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.flowcommon.value.FControlValue
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

/**
 * adapter for setting control command to elements of device
 * @param context
 * @param action: type of action control(on/off or lock/unlock or ....)
 */
class AdapterConfiguredControlCmd(
    context: Context,
    private  val action: Int,
    private val onCmdsChanged: () -> Unit,
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
    // map of control commands to devices. key: uuid of device, value: array of control command
    private var deviceControlCmdMap: HashMap<String?, Array<FControlValue>> = hashMapOf()

    // return map of control commands
    fun getDeviceControlCmdMap(): HashMap<String?, Array<FControlValue>> {
        return deviceControlCmdMap
    }
    private lateinit var adapterControlElement: AdapterControlElement

    /**
     * handle when select or unselect a command
     * @param device the device that is being set the control command
     * @param newCmd The new control command that is setted to device
     */
    private fun handleAddAndRemoveAction(
        device: IoTDevice,
        newCmd: FControlValue
    ) {
        // get the current commands of devices
        var cmds: Array<FControlValue>? = deviceControlCmdMap[device.uuid]
        // check if the the command is already setted to the device
        if (cmds != null) {
            val cmd = cmds.firstOrNull {
                it.elm == newCmd.elm
            }
            cmds = if (cmd != null) {
                if (cmd.attrValue.contentEquals(newCmd.attrValue)) {
                    cmds.filterNot { it == cmd }.toTypedArray()
                } else {
                    cmds.filterNot { it == cmd }.plus(newCmd).toTypedArray()
                }
            } else {
                cmds.plus(newCmd)
            }
        } else {
            cmds = arrayOf(newCmd)
        }
        if (cmds.isNotEmpty()) {
            deviceControlCmdMap[device.uuid] = cmds
        } else {
            deviceControlCmdMap.remove(device.uuid)
        }
        onCmdsChanged.invoke()
    }

    /**
     * view holder of device that has one element
     */
    inner class SingleViewHolder(private val binding: LayoutItemSetControlActionSingleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(deviceEntry: Map.Entry<String?, IntArray>) {
            binding.apply {
                val device = FlowSdk.deviceHandler().get(deviceEntry.key)
                if (flControl.isShown) {
                    btnExpand.setImageResource(R.drawable.ic_downward_full)
                } else {
                    btnExpand.setImageResource(R.drawable.ic_up)
                }
                device?.let {
                    txtLabel.text = device.label
                    val location = FlowSdk.locationHandler().get(device.locationId)
                    txtLocation.text = location?.label?: ""

                    //handle action when user wanna set on/off command
                    lnControlOnOff.initView(onOnOffSelected = { newActionValue ->
                        val elm = device.elementIds.first()

                        val newCtlValue = FControlValue(
                            device.eid,
                            elm,
                            newActionValue
                        )

                        handleAddAndRemoveAction(
                            device,
                            newCtlValue
                        )
                        cbConfiged.isChecked = deviceControlCmdMap[device.uuid]?.isEmpty() == false
                        //refresh button state
                        lnControlOnOff.updateButtonState(deviceControlCmdMap[device.uuid]?.find { it == newCtlValue }?.attrValue)
                    })
                }

                root.setOnClickListener {
                    if (flControl.isShown) {
                        btnExpand.setImageResource(R.drawable.ic_downward_full)

                        // Animation trượt lên để ẨN VIEW
                        val slideUp = TranslateAnimation(0f, 0f, 0f, -flControl.height.toFloat())
                        slideUp.duration = 300
                        slideUp.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                                // Chỉ ẩn view sau khi animation kết thúc
                                flControl.visibility = android.view.View.GONE
                            }
                        })
                        flControl.startAnimation(slideUp)

                    } else {
                        btnExpand.setImageResource(R.drawable.ic_up)

                        // Animation trượt xuống để HIỆN VIEW
                        // 1. Đặt view ở trạng thái VISIBLE để nó có thể được vẽ
                        flControl.visibility = android.view.View.VISIBLE
                        // 2. Tạo animation trượt từ trên xuống
                        val slideDown = TranslateAnimation(0f, 0f, -flControl.height.toFloat(), 0f)
                        slideDown.duration = 300
                        // 3. Bắt đầu animation
                        flControl.startAnimation(slideDown)
                    }
                }
            }
        }
    }

    /**
     * view holder of device that has more than 1 element
     */
    inner class GridViewHolder(private val binding: LayoutItemSetControlActionGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(deviceEntry: Map.Entry<String?, IntArray>) {
            binding.apply {
                adapterControlElement = AdapterControlElement(
                    deviceEntry.key,
                    action,
                    onCmdSelected = {
                        cbConfiged.isChecked = deviceControlCmdMap[deviceEntry.key]?.isEmpty() == false
                    }
                )
                //show information of device
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

    /**
     * adapter for setting control command to elements of device
     * @param devId: uuid of the device
     * @param action: type of control command(on/off or lock/unlock)
     * @param onCmdSelected: The callback to be invoked after user select or unselect a command
     */
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
                    // show information of element
                    txtLabel.text = elmInfo.value.label?: "Nút ${elmInfo.key}"

                    when(action) {
                        IoTAttribute.ACT_ONOFF -> lnControlOnOff.show()
                    }
                    //handle control action on/off of element
                    lnControlOnOff.initView(onOnOffSelected = { newActionValue ->
                        val device = FlowSdk.deviceHandler().get(devId)
                        device?.let {
                            val newCtlValue = FControlValue(
                                device.eid,
                                elmInfo.key,
                                newActionValue
                            )

                            handleAddAndRemoveAction(
                                device,
                                newCtlValue
                            )

                            onCmdSelected.invoke()
                            // refresh button state
                            lnControlOnOff.updateButtonState(deviceControlCmdMap[device.uuid]?.find { it == newCtlValue }?.attrValue)
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