package com.example.thingsflow.ui.deviceConfig.zigbee

import android.text.Editable
import android.text.TextWatcher
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
import com.example.thingsflow.databinding.LayoutItemConfigZigbeeDeviceBinding
import com.example.thingsflow.module.model.ConfigZigbeeDeviceModel
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterSpinnerGroup
import com.example.thingsflow.ui.dialog.DialogCreateGroup
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

@AndroidEntryPoint
class FragmentConfigZigbeeDevices : FragmentBase<FragmentConfigZigbeeDevicesBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_config_zigbee_devices
    private val adapterConfigZigbeeDevice: AdapterConfigZigbeeDevice by lazy {
        AdapterConfigZigbeeDevice(
            onCreateGroup = {
                dialogCreateGroup.show()
            }
        )
    }
    private val deviceList = arrayListOf<ConfigZigbeeDeviceModel>()
    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    private val dialogCreateGroup: DialogCreateGroup by lazy {
        DialogCreateGroup(
            requireContext()
        )
    }
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            deviceList.addAll(vmConfigZigbee.getSyncingZigbeeDevices())
            rvConfigedDevices.adapter = adapterConfigZigbeeDevice
            adapterConfigZigbeeDevice.submitList(deviceList)
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentSelectZigbeeGateway)
            }
            btnFinish.setOnClickListener {
                vmConfigZigbee.setSyncingZigbeeDevices(deviceList)
                findNavController().navigate(R.id.fragmentSyncZigbeeDevices)
            }
        }
    }

    class AdapterConfigZigbeeDevice(
        private val onCreateGroup: () -> Unit
    )
        : ListAdapter<ConfigZigbeeDeviceModel, AdapterConfigZigbeeDevice.ConfigZigbeeDeviceViewHolder>(
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
        inner class ConfigZigbeeDeviceViewHolder(
            private val binding: LayoutItemConfigZigbeeDeviceBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun onBind(device: ConfigZigbeeDeviceModel) {
                binding.apply {
                    val label = device.device?.ioTProductModel?.name
                    edtLabel.setText(label)
                    device.label = label
                    val availableGroups = arrayListOf<IoTGroup?>()
                    availableGroups.add(null)
                    availableGroups.add(
                        IoTGroup().apply {
                            uuid = "-1"
                        }
                    )
                    availableGroups.addAll(FlowSdk.groupHandler().all)
                    val adapterSpinnerGroup: AdapterSpinnerGroup = AdapterSpinnerGroup(
                        binding.root.context,
                        availableGroups
                    )
                    spinnerGroup.adapter = adapterSpinnerGroup
                    spinnerGroup.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedGroup = adapterSpinnerGroup.getItem(position)
                                if (selectedGroup?.uuid?.contentEquals("-1") == true) {
                                    onCreateGroup.invoke()
                                } else {
                                    device.groupId = selectedGroup?.uuid
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }

                    edtLabel.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {

                        }

                        override fun afterTextChanged(s: Editable?) {
                            device.label = s.toString()
                        }
                    })
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ConfigZigbeeDeviceViewHolder {
            val inflater = LayoutItemConfigZigbeeDeviceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ConfigZigbeeDeviceViewHolder(inflater)
        }

        override fun onBindViewHolder(
            holder: ConfigZigbeeDeviceViewHolder,
            position: Int
        ) {
            holder.onBind(getItem(position))
        }
    }
}