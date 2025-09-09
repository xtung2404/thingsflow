package com.example.thingsflow.ui.deviceConfig.zigbee

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentIdentifyDeviceBinding
import com.example.thingsflow.databinding.FragmentIdentifyZigbeeDeviceBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDiscoveredDevices
import com.example.thingsflow.ui.adapter.AdapterDiscoveredZigbeeDevices
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.callback.PairZigbeeDeviceCallback
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZ2mDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

@AndroidEntryPoint
class FragmentIdentifyZigbeeDevice : FragmentBase<FragmentIdentifyZigbeeDeviceBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_identify_zigbee_device
    private val TAG = "FragmentIdentifyZigbeeDevice"
    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    private val discoveredZigbeeDevices = arrayListOf<IoTPairedZigbeeDevice>()
    private val ioTPairedZigbeeDevice: IoTPairedZigbeeDevice?= null
    private val adapterDiscoveredZigbeeDevices: AdapterDiscoveredZigbeeDevices by lazy {
        AdapterDiscoveredZigbeeDevices(
            onItemSelected = {
                val bundle = bundleOf("gatewayId" to gatewayId, "pairedDevice" to ioTPairedZigbeeDevice)
                findNavController().navigate(R.id.fragmentSetZigbeeDeviceLabel, bundle)
            }
        )
    }
    private var gatewayId: String?= null

    override fun initVariable() {
        super.initVariable()
        discoveredZigbeeDevices.clear()
        arguments?.let {
            gatewayId = it.getString("gatewayId")
        }
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
            rvGateway.adapter = adapterDiscoveredZigbeeDevices
            adapterDiscoveredZigbeeDevices.submitList(discoveredZigbeeDevices)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            startPairingZigbee()
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnRescan.setOnClickListener {
                startPairingZigbee()
            }
        }
    }

    fun startPairingZigbee() {
        vmConfigZigbee.startPairingDevice(
            gatewayId!!,
            60,
            0,
            object : PairZigbeeDeviceCallback {
                override fun onPairingStatus(p0: Int) {

                }

                override fun onPairedDevice(p0: IoTPairedZigbeeDevice?) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (discoveredZigbeeDevices.isEmpty()) {
                            binding.lnSelectDevice.visibility = View.VISIBLE
                            binding.clScanning.visibility = View.GONE
                        }
                        if (!discoveredZigbeeDevices.contains(p0)) {
                            p0?.let {
                                ILogR.D(TAG, "discovery:deviceFound ", it.ioTProductModel.name)
                                discoveredZigbeeDevices.add(p0)
                                adapterDiscoveredZigbeeDevices.notifyItemInserted(discoveredZigbeeDevices.size)
                            }
                        }
                    }
                }

                override fun onPairedDevice(p0: IoTPairedZ2mDevice?) {

                }

                override fun onPairedUnknownDevice(
                    p0: String?,
                    p1: String?,
                    p2: String?
                ) {

                }

                override fun onNotDevicePaired() {

                }
            }
        )
    }
}