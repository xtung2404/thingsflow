package com.example.thingsflow.ui.deviceConfig.zigbee

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentIdentifyZigbeeDeviceBinding
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDiscoveredZigbeeDevices
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.PairZigbeeDeviceCallback
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZ2mDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

@AndroidEntryPoint
class FragmentIdentifyZigbeeDevice : FragmentBase<FragmentIdentifyZigbeeDeviceBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_identify_zigbee_device
    private val TAG = "FragmentIdentifyZigbeeDevice"
    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    private val discoveredZigbeeDevices: HashMap<IoTPairedZigbeeDevice, Boolean> = hashMapOf()
    private val scanningTime: Long = 120
    private val adapterDiscoveredZigbeeDevices: AdapterDiscoveredZigbeeDevices by lazy {
        AdapterDiscoveredZigbeeDevices(
            false,
            onItemSelected = {

            }
        )
    }
    private var gatewayId: String? = null

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
            discoveredZigbeeDevices.clear()
            btnRescan.visibility = View.GONE
            toolbar.txtTitle.text = getFragmentLabel(
                requireContext(),
                findNavController().previousBackStackEntry?.destination?.id
            )
            gatewayId?.let {
                val gateway = SmartSdk.deviceHandler().get(it)
                val location = SmartSdk.locationHandler().get(gateway.locationId)
                val group = SmartSdk.groupHandler().get(gateway.groupId)
                gateway?.let {
                    txtLabel.text = gateway.label
                }
                location?.let {
                    txtLocation.text = location.label
                }
                txtGroup.text = if (group == null)
                    resources.getString(R.string.device_has_not_been_assigned_to_group) else
                    group.label
            }
            txtScanning.text = resources.getString(R.string.scanning_device)
            clScanning.visibility = View.VISIBLE
            lnSelectDevice.visibility = View.GONE
            btnRescan.isEnabled = false
            // set height of recyclerview equals 7/10 of screen's height
//            val maxHeight = (resources.displayMetrics.heightPixels * 0.7).toInt()
//            rvGateway.viewTreeObserver.addOnGlobalLayoutListener {
//                if (rvGateway.height > maxHeight) {
//                    rvGateway.layoutParams.height = maxHeight
//                    rvGateway.requestLayout()
//                }
//            }
            rvGateway.adapter = adapterDiscoveredZigbeeDevices
            adapterDiscoveredZigbeeDevices.submitList(discoveredZigbeeDevices.entries.toList())
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            startPairingZigbee()
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentSelectZigbeeGateway)
            }

            btnRescan.setOnClickListener {
                btnRescan.visibility = View.GONE
                startPairingZigbee()
            }

            btnContinue.setOnClickListener {
                vmConfigZigbee.setScannedZigbeeDevices(discoveredZigbeeDevices.keys.toList())
                findNavController().navigate(R.id.fragmentSelectPairedZigbee)
            }
        }
    }

    fun startPairingZigbee() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.txtScanning.text = resources.getString(R.string.scanning_device)
            vmConfigZigbee.startPairingDevice(
                gatewayId!!,
                scanningTime.toInt(),
                0,
                object : PairZigbeeDeviceCallback {

                    override fun onPairingStatus(p0: Int, p1: String?) {

                    }

                    override fun onPairingFinished() {

                    }

                    override fun onNewDevicePaired(p0: IoTPairedZigbeeDevice?) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (discoveredZigbeeDevices.isEmpty()) {
                                binding.lnSelectDevice.visibility = View.VISIBLE
                                binding.clScanning.visibility = View.GONE
                            }
                            if (!discoveredZigbeeDevices.contains(p0)) {
                                p0?.let {
                                    ILogR.D(TAG, "discovery:deviceFound ", it.ioTProductModel.name)
                                    discoveredZigbeeDevices[it] = false
                                    binding.txtDeviceFound.text = "${discoveredZigbeeDevices.size}"
                                    adapterDiscoveredZigbeeDevices.submitList(discoveredZigbeeDevices.entries.toList())
                                }
                            }
                        }
                    }

                    override fun onNewDevicePaired(p0: IoTPairedZ2mDevice?) {

                    }

                    override fun onNewDevicePairedUnknown(
                        p0: String?,
                        p1: String?,
                        p2: String?
                    ) {

                    }

                    override fun onNotFoundDevicePaired() {

                    }
//                    override fun onPairingStatus(p0: Int, p1: String?) {
//
//                    }
//
//                    override fun onPairingFinished() {
//
//                    }
//
//                    override fun onNewDevicePaired(p0: IoTPairedZigbeeDevice?) {
//
//                    }
//
//                    override fun onNewDevicePaired(p0: IoTPairedZ2mDevice?) {
//
//                    }
//
//                    override fun onNewDevicePairedUnknown(
//                        p0: String?,
//                        p1: String?,
//                        p2: String?
//                    ) {
//
//                    }
//
//                    override fun onNotFoundDevicePaired() {
//
//                    }
                }
            )

            // 🔥 Countdown loop
            for (i in scanningTime downTo 0) {
                binding.txtScanningTime.text = "$i s "
                binding.txtScanningTime2.text = "$i s "
                delay(1000)
                if (i == 0L) {
                    binding.btnRescan.visibility = View.VISIBLE
                }
            }

            // Hết thời gian -> kiểm tra xem có tìm thấy thiết bị nào không
            if (discoveredZigbeeDevices.isEmpty()) {
                binding.txtScanning.text = getString(R.string.no_device_found)
                binding.btnRescan.isEnabled = true
            }
        }
    }
}