package com.example.thingsflow.ui.deviceConfig

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentConfigWiFiBinding
import com.example.thingsflow.module.viewmodel.ConfigWileDirectViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.DiscoveredWiFiAdapter
import com.example.thingsflow.ui.dialog.DialogConfigWiFi
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.entity.IoTWifiInfo

@AndroidEntryPoint
class ConfigWiFiFragment : BaseFragment<FragmentConfigWiFiBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_config_wi_fi

    private val configWileDirectViewModel by activityViewModels<ConfigWileDirectViewModel>()
    private val discoveredWiFiAdapter: DiscoveredWiFiAdapter by lazy {
        DiscoveredWiFiAdapter(
            onWiFiSelected = {
                dialogConfigWiFi.show(
                    it.ssid
                )
            }
        )
    }

    private val dialogConfigWiFi: DialogConfigWiFi by lazy {
        DialogConfigWiFi(
            requireContext(),
            requireActivity(),
            onConfigSuccess = {
                findNavController().navigate(R.id.assignDeviceToGroupFragment)
            }
        )
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(requireContext(), findNavController().previousBackStackEntry?.destination?.id)
        }
    }
    override fun initAction() {
        super.initAction()
        binding.apply {
            scanWifi()

            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnRescan.setOnClickListener {
                scanWifi()
            }

            btnConnectToAnotherWifi.setOnClickListener {
                dialogConfigWiFi.show(null)
            }
        }
    }

    override fun initVariable() {
        super.initVariable()
        binding.apply {
            rvWifi.adapter = discoveredWiFiAdapter
        }
    }

    /**
     * request device to scan for available WiFis in spanning time of 8 seconds
     */
    fun scanWifi() {
        discoveredWiFiAdapter.submitList(listOf())
        configWileDirectViewModel.scanWiFi(
            object: RequestCallback<Collection<IoTWifiInfo>> {
                override fun onSuccess(p0: Collection<IoTWifiInfo>?) {
                    p0?.let {
                        discoveredWiFiAdapter.submitList(it.toList())
                    }
                }

                override fun onFailure(p0: Int, p1: String?) {

                }
            }
        )
    }


}