package com.example.thingsflow.ui.deviceConfig

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentIdentifyDeviceBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDiscoveredDevices
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback

@AndroidEntryPoint
class FragmentIdentifyDevice : FragmentBase<FragmentIdentifyDeviceBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_identify_device
    private val TAG = "IdentifyDeviceFragment"
    // Time to discover for available devices
    private val DISCOVERY_TIMEOUT_SECONDS: Long = 15
    private val vmConfigWileDirect by activityViewModels<VMConfigWileDirect>()
    // list of discovered devices
    private val discoveredGateways = arrayListOf<IoTDirectDeviceInfo>()
    private val adapterDiscoveredDevices: AdapterDiscoveredDevices by lazy {
        AdapterDiscoveredDevices(
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
            txtScanning.text = resources.getString(R.string.scanning_gateway)
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
            rvGateway.adapter = adapterDiscoveredDevices
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            discovery()

            toolbar.btnBack.setOnClickListener {
                vmConfigWileDirect.cancelDiscovery()
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
        adapterDiscoveredDevices.submitList(discoveredGateways)
        vmConfigWileDirect
            .discovery(
                DISCOVERY_TIMEOUT_SECONDS,
                object : ScanningIoTDeviceCallback {
                    override fun onDeviceFound(device: IoTDirectDeviceInfo) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (discoveredGateways.isEmpty()) {
                                binding.lnSelectDevice.visibility = View.VISIBLE
                                binding.clScanning.visibility = View.GONE
                            }
                            if (!discoveredGateways.contains(device)) {
                                ILogR.D(TAG, "discovery:deviceFound ", device.label)
                                discoveredGateways.add(device)
                                adapterDiscoveredDevices.notifyItemInserted(discoveredGateways.size - 1)
                            }
                        }
                    }

                    override fun onTimeOut() {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.txtScanning.text = "Không tìm thấy gateway nào!"
                            binding.btnRescan.isEnabled = true
                        }
                    }
                }
            )
    }

    /**
     * connect to one of discovered devices
     */
    private fun identifyAndConnectToDevice(ioTDirectDeviceInfo: IoTDirectDeviceInfo) {
        vmConfigWileDirect.connectAndIdentifyDevice(
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
        adapterDiscoveredDevices.submitList(discoveredGateways)
    }
}