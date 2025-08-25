package com.example.thingsflow.ui.device

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentDeviceBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDevices
import rogo.iot.module.rogocore.sdk.SmartSdk

class FragmentDevice : FragmentBase<FragmentDeviceBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_device

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
            rvDevice.adapter = adapterDevices
            adapterDevices.submitList(SmartSdk.deviceHandler().all.toList())
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            btnConnect.setOnClickListener {
                findNavController().navigate(R.id.identifyDeviceFragment)
            }
        }
    }

    override fun initAction() {
        super.initAction()

    }
}