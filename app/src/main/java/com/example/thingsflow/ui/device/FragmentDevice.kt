package com.example.thingsflow.ui.device

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentDeviceBinding
import com.example.thingsflow.module.viewmodel.VMGroup
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterGroupHorizontal
import com.example.thingsflow.ui.adapter.AdapterSpinnerLocation
import com.example.thingsflow.ui.dialog.DialogDeviceType
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

@AndroidEntryPoint
class FragmentDevice : FragmentBase<FragmentDeviceBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_device

    private val TAG = "FragmentDevice"
    private val vmLocation by activityViewModels <VMLocation>()
    private val vmGroup by viewModels<VMGroup>()

    private lateinit var adapterSpinnerLocation: AdapterSpinnerLocation
    private val adapterGroupHorizontal: AdapterGroupHorizontal by lazy {
        AdapterGroupHorizontal(
            onItemClick = {

            }
        )
    }
    private val dialogDeviceType: DialogDeviceType by lazy {
        DialogDeviceType(
            requireContext(),
            onDeviceTypeSelected =  {devType ->
                when(devType) {
                    IoTDeviceType.GATEWAY -> {
                        findNavController().navigate(R.id.locationManagementFragment)
                    }
                    else -> {
                        findNavController().navigate(R.id.fragmentSelectZigbeeGateway)
                    }
                }
        })
    }

    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDeviceSelected = { uuid, elm ->
                val bundle = bundleOf("device" to uuid)
                findNavController().navigate(R.id.fragmentDeviceDetail, bundle)
            }
        )
    }
    override fun initVariable() {
        super.initVariable()
        binding.apply {
//            rvDevice.adapter = adapterDevices
//            adapterDevices.submitList(SmartSdk.deviceHandler().all.toList())

        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            vmLocation.refresh()
            vmLocation.locationsLiveData.observe(requireActivity()) {
                adapterSpinnerLocation = AdapterSpinnerLocation(requireContext(), it)
            }
            spinnerLocation.adapter = adapterSpinnerLocation
            rvGroup.adapter = adapterGroupHorizontal
            val availableGroup = arrayListOf<IoTGroup?>()
            availableGroup.add(null)
            availableGroup.addAll(vmGroup.getAll())
            adapterGroupHorizontal.submitList(availableGroup)


            btnAdd.setOnClickListener {
                dialogDeviceType.show()
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLocation = adapterSpinnerLocation.getItem(position)
                    ILogR.D(TAG, "onSpinnerLocationSelected:", selectedLocation?.label)
                    selectedLocation?.let {
                        vmLocation.setDefaultLocation(it.uuid)
                        val availableGroup = arrayListOf<IoTGroup?>()
                        availableGroup.add(null)
                        availableGroup.addAll(vmGroup.getAll())
                        adapterGroupHorizontal.submitList(availableGroup)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }
}