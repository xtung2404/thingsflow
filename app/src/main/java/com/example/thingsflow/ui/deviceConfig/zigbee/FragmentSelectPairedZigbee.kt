package com.example.thingsflow.ui.deviceConfig.zigbee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSelectPairedZigbeeBinding
import com.example.thingsflow.module.model.ConfigZigbeeDeviceModel
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterDiscoveredZigbeeDevices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

class FragmentSelectPairedZigbee : FragmentBase<FragmentSelectPairedZigbeeBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_select_paired_zigbee

    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    private var scannedZigbeeMap: HashMap<IoTPairedZigbeeDevice, Boolean> = hashMapOf()
    private val adapterDiscoveredZigbeeDevices: AdapterDiscoveredZigbeeDevices by lazy {
        AdapterDiscoveredZigbeeDevices(
            true,
            onItemSelected = {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.txtNumberSelected.text = "${adapterDiscoveredZigbeeDevices.getSelectedDevices().size}"
                }
            }
        )
    }
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            scannedZigbeeMap.clear()
            scannedZigbeeMap = vmConfigZigbee.getScannedZigbeeDevices().associateWith { false } as HashMap<IoTPairedZigbeeDevice, Boolean>
            rvDevices.adapter = adapterDiscoveredZigbeeDevices
            adapterDiscoveredZigbeeDevices.submitList(scannedZigbeeMap.entries.toList())
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
                if (adapterDiscoveredZigbeeDevices.getSelectedDevices().isEmpty()) {
                    return@setOnClickListener
                }
                val selectedDevices = arrayListOf<ConfigZigbeeDeviceModel>()
                adapterDiscoveredZigbeeDevices.getSelectedDevices().forEach {
                    selectedDevices.add(
                        ConfigZigbeeDeviceModel(
                            it,
                            it.ioTProductModel.name,
                            null
                        )
                    )
                }
                vmConfigZigbee.setSyncingZigbeeDevices(selectedDevices)
                findNavController().navigate(R.id.fragmentConfigZigbeeDevices)
            }
        }
    }
}