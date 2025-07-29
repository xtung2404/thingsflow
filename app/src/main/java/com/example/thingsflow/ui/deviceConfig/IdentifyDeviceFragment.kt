package com.example.thingsflow.ui.deviceConfig

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentIdentifyDeviceBinding
import com.example.thingsflow.module.viewmodel.ConfigWileDirectViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.DiscoveredDevicesAdapter
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.platform.entity.IoTNetworkConnectivity
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.DiscoverySmartDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SetupWileDirectDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback

@AndroidEntryPoint
class IdentifyDeviceFragment : BaseFragment<FragmentIdentifyDeviceBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_identify_device
    private val TAG = "IdentifyDeviceFragment"
    // Time to discover for available devices
    private val DISCOVERY_TIMEOUT_SECONDS: Long = 15
    private val configWileDirectViewModel by activityViewModels<ConfigWileDirectViewModel>()
    // list of discovered devices
    private val discoveredGateways = arrayListOf<IoTDirectDeviceInfo>()
    private val discoveredDevicesAdapter: DiscoveredDevicesAdapter by lazy {
        DiscoveredDevicesAdapter(
            onItemSelected = {
                identifyAndConnectToDevice(it)
            }
        )
    }
    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(
                requireContext(),
                findNavController().previousBackStackEntry?.destination?.id
            )
            clScanning.visibility = View.VISIBLE
            lnSelectDevice.visibility = View.GONE
            btnRescan.isEnabled = false
            // set height of recyclerview equals 7/10 of screen's height
            val maxHeight = (resources.displayMetrics.heightPixels * 0.7).toInt()
            rvGateway.viewTreeObserver.addOnGlobalLayoutListener {
                if (rvGateway.height > maxHeight) {
                    rvGateway.layoutParams.height = maxHeight
                    rvGateway.requestLayout()
                }
            }
            rvGateway.adapter = discoveredDevicesAdapter
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            discovery()

            toolbar.btnBack.setOnClickListener {
                configWileDirectViewModel.cancelDiscovery()
                findNavController().popBackStack()
            }

            btnRescan.setOnClickListener {
                discovery()
            }
        }
    }

    /**
     * discovery for available devices nearby in spanning time of 15 seconds
     * the callback can be triggered by a device several times so it needs filtering
     */
    private fun discovery() {
        discoveredGateways.clear()
        discoveredDevicesAdapter.submitList(discoveredGateways)
        configWileDirectViewModel
            .discovery(
                DISCOVERY_TIMEOUT_SECONDS,
                object : ScanningIoTDeviceCallback {
                    override fun onDeviceFound(device: IoTDirectDeviceInfo) {
                        if (discoveredGateways.isEmpty()) {
                            binding.lnSelectDevice.visibility = View.VISIBLE
                            binding.clScanning.visibility = View.GONE
                        }
                        if (!discoveredGateways.contains(device)) {
                            ILogR.D(TAG, "discovery:deviceFound ", device.label)
                            discoveredGateways.add(device)
                            discoveredDevicesAdapter.notifyItemInserted(discoveredGateways.size - 1)
                        }
                    }

                    override fun onTimeOut() {
                        binding.btnRescan.isEnabled = true
                    }
                }
            )
    }

    /**
     * connect to one of discovered devices
     */
    private fun identifyAndConnectToDevice(ioTDirectDeviceInfo: IoTDirectDeviceInfo) {
        configWileDirectViewModel.connectAndIdentifyDevice(
            ioTDirectDeviceInfo,
            object : SuccessStatusCallback {
                override fun onSuccess() {
                    CoroutineScope(Dispatchers.Main).launch {
                        findNavController().navigate(R.id.configWiFiFragment)
                    }
                }

                override fun onFailure(p0: Int, p1: String?) {

                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        discoveredGateways.clear()
        discoveredDevicesAdapter.submitList(discoveredGateways)
    }
}