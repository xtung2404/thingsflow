package com.example.thingsflow.ui.deviceConfig.gateway

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentConfigWiFiBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDiscoveredWiFi
import com.example.thingsflow.ui.dialog.DialogConfigWiFi
import com.example.thingsflow.ui.dialog.DialogConfigWiFiManually
import com.example.thingsflow.ui.dialog.DialogSelectConnectivity
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.base.entity.IoTNetworkConnectivity
import rogo.iot.module.base.entity.IoTWifiInfo

@AndroidEntryPoint
class FragmentConfigWiFi : FragmentBase<FragmentConfigWiFiBinding>() {
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
            requireActivity(),
            onConfigSuccess = {
                CoroutineScope(Dispatchers.Main).launch {
                    val map = vmConfigWileDirect.getSupportedConnectivities()
                    map.filter { it.key.infType == IoTNetworkConnectivity.WIFI }
                        .forEach { entry ->
                            map[entry.key] = true
                        }
                    dialogSelectConnectivity.show(map)
                }

            }
        )
    }

    private val dialogConfigWiFiManually: DialogConfigWiFiManually by lazy {
        DialogConfigWiFiManually(
            requireActivity(),
            onConfigSuccess = {
                CoroutineScope(Dispatchers.Main).launch {
                    val map = vmConfigWileDirect.getSupportedConnectivities()
                    map.filter { it.key.infType == IoTNetworkConnectivity.WIFI }
                        .forEach { entry ->
                            map[entry.key] = true
                        }
                    dialogSelectConnectivity.show(map)
                }
            }
        )
    }

    private val dialogSelectConnectivity: DialogSelectConnectivity by lazy {
        DialogSelectConnectivity(
            requireContext(),
            onConnectivitySelected = {
            },
            onFinish = {
                dialogSelectConnectivity.dismiss()
                findNavController().navigate(R.id.fragmentConfigGateway)
            }
        )
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(requireContext(), findNavController().previousBackStackEntry?.destination?.id)
            vmConfigWileDirect.getIdentifiedDevice()?.let {
                txtLabel.text = it.label
            }
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
                dialogConfigWiFiManually.show()
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
            object: RequestResultCallback<Collection<IoTWifiInfo>> {
                override fun onResult(p0: Collection<IoTWifiInfo>?) {
                    p0?.let {
                        adapterDiscoveredWiFi.submitList(it.toList())
                    }
                }

                override fun onError(p0: Int) {

                }
            }
        )
    }


}