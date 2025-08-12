package com.example.thingsflow.ui.deviceConfig

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentConfigWiFiBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.AdapterDiscoveredWiFi
import com.example.thingsflow.ui.dialog.DialogConfigWiFi
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.entity.IoTWifiInfo

@AndroidEntryPoint
class FragmentConfigWiFi : BaseFragment<FragmentConfigWiFiBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_config_wi_fi

    private val vmConfigWileDirect by activityViewModels<VMConfigWileDirect>()
    private val adapterDiscoveredWiFi: AdapterDiscoveredWiFi by lazy {
        AdapterDiscoveredWiFi(
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
            rvWifi.adapter = adapterDiscoveredWiFi
        }
    }

    /**
     * request device to scan for available WiFis in spanning time of 8 seconds
     */
    fun scanWifi() {
        adapterDiscoveredWiFi.submitList(listOf())
        vmConfigWileDirect.scanWiFi(
            object: RequestCallback<Collection<IoTWifiInfo>> {
                override fun onSuccess(p0: Collection<IoTWifiInfo>?) {
                    p0?.let {
                        adapterDiscoveredWiFi.submitList(it.toList())
                    }
                }

                override fun onFailure(p0: Int, p1: String?) {

                }
            }
        )
    }


}