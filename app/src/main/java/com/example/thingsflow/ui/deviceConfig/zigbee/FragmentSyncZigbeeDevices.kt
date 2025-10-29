package com.example.thingsflow.ui.deviceConfig.zigbee

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentConfigZigbeeDevicesBinding
import com.example.thingsflow.databinding.FragmentSyncZigbeeDevicesBinding
import com.example.thingsflow.databinding.LayoutItemConfigZigbeeDeviceBinding
import com.example.thingsflow.databinding.LayoutItemDiscoveredDeviceBinding
import com.example.thingsflow.databinding.LayoutItemSyncingDeviceBinding
import com.example.thingsflow.module.model.ConfigZigbeeDeviceModel
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterSpinnerGroup
import com.example.thingsflow.ui.adapter.AdapterSpinnerLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTGroup
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

@AndroidEntryPoint
class FragmentSyncZigbeeDevices : FragmentBase<FragmentSyncZigbeeDevicesBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_sync_zigbee_devices
    private val TAG = "FragmentSyncZigbeeDevices"
    private val adapterSyncingZigbeeDevice: AdapterSyncingZigbeeDevice by lazy {
        AdapterSyncingZigbeeDevice()
    }
    private var pos: Int = 0
    private val deviceList = arrayListOf<ConfigZigbeeDeviceModel>()

    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            deviceList.clear()
            deviceList.addAll(vmConfigZigbee.getSyncingZigbeeDevices())
            rvSyncingDevices.adapter = adapterSyncingZigbeeDevice
            adapterSyncingZigbeeDevice.submitList(deviceList)
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            pos = 0
            syncingDevice(pos)
            txtTitle.text = getString(R.string.syncing_devices_to_cloud)
            txtSubtitle.text = getString(R.string.wait_a_little)
            txtSubtile1.text = getString(R.string.devices_are_being_added)
            lnAdding.visibility = View.GONE
            lnFinishing.visibility = View.VISIBLE
        }
    }

    private val requestCallback: RequestCallback<IoTDevice> = object : RequestCallback<IoTDevice> {
        override fun onSuccess(p0: IoTDevice?) {
            ILogR.D(TAG, "onAdd", "onSuccess")
            CoroutineScope(Dispatchers.Main).launch {
                adapterSyncingZigbeeDevice.updateItemStatus(deviceList[pos], true)
            }
            if (pos < deviceList.size - 1) {
                pos += 1
                syncingDevice(pos)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.apply {
                        txtTitle.text = getString(R.string.finishing_adding_devices)
                        txtSubtitle.text = getString(R.string.you_can_continue_or_add_another_device)
                        txtSubtile1.text = getString(R.string.list_of_device)
                        lnAdding.visibility = View.GONE
                        lnFinishing.visibility = View.VISIBLE
                    }
                }
            }
        }

        override fun onFailure(p0: Int, p1: String?) {
            ILogR.D(TAG, "onFailure", "onFailure")
            CoroutineScope(Dispatchers.Main).launch {
                adapterSyncingZigbeeDevice.updateItemStatus(deviceList[pos], false)
            }
            if (pos < deviceList.size - 1) {
                pos += 1
                syncingDevice(pos)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.apply {
                        txtTitle.text = getString(R.string.finishing_adding_devices)
                        txtSubtitle.text = getString(R.string.you_can_continue_or_add_another_device)
                        txtSubtile1.text = getString(R.string.list_of_device)
                        lnAdding.visibility = View.GONE
                        lnFinishing.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun syncingDevice(position: Int) {
        ILogR.D(TAG, "syncingDevice")
        vmConfigZigbee.syncDeviceToCloud(
            vmConfigZigbee.getSelectedGateway()!!,
            deviceList[position].device!!,
            deviceList[position].label!!,
            deviceList[position].groupId,
            deviceList[position].device?.ioTProductModel?.devSubType?: 0,
            requestCallback
        )
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnCancel.setOnClickListener {

            }

            btnFinish.setOnClickListener {
                findNavController().navigate(R.id.fragmentDevice)
            }
        }
    }

    class AdapterSyncingZigbeeDevice() :
        ListAdapter<ConfigZigbeeDeviceModel, AdapterSyncingZigbeeDevice.SyncingZigbeeDeviceViewHolder>(
            object : DiffUtil.ItemCallback<ConfigZigbeeDeviceModel>() {
                override fun areItemsTheSame(
                    oldItem: ConfigZigbeeDeviceModel,
                    newItem: ConfigZigbeeDeviceModel
                ): Boolean {
                    return oldItem.device == newItem.device && oldItem.label == newItem.label && oldItem.groupId == newItem.groupId
                }

                override fun areContentsTheSame(
                    oldItem: ConfigZigbeeDeviceModel,
                    newItem: ConfigZigbeeDeviceModel
                ): Boolean {
                    return false
                }
            }
        ) {
        private val statusMap = mutableMapOf<IoTPairedZigbeeDevice, Int>()
        inner class SyncingZigbeeDeviceViewHolder(
            private val binding: LayoutItemSyncingDeviceBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun onBind(device: ConfigZigbeeDeviceModel) {
                binding.apply {
                    val group = FlowSdk.groupHandler().get(device.groupId)
                    txtLabel.text = device.label
                    txtDesc.text = if (group == null) root.context.getString(R.string.device_has_not_been_assigned_to_group) else group.label
                    val icon = statusMap[device.device] ?: R.drawable.ic_progress
                    imgStatus.setImageResource(icon)
                }
            }
        }


        fun updateItemStatus(model: ConfigZigbeeDeviceModel, isAddedSuccessfully: Boolean) {
            val index = currentList.indexOfFirst { it.device == model.device }
            if (index != -1) {
                statusMap[model.device!!] = if (isAddedSuccessfully) R.drawable.ic_success else R.drawable.ic_failure
                notifyItemChanged(index)
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SyncingZigbeeDeviceViewHolder {
            val inflater = LayoutItemSyncingDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return SyncingZigbeeDeviceViewHolder(inflater)
        }

        override fun onBindViewHolder(
            holder: SyncingZigbeeDeviceViewHolder,
            position: Int
        ) {
            holder.onBind(getItem(position))
        }
    }
}