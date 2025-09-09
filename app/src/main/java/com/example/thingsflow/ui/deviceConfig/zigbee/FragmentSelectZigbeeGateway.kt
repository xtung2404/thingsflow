package com.example.thingsflow.ui.deviceConfig.zigbee

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentIdentifyDeviceBinding
import com.example.thingsflow.databinding.FragmentSelectZigbeeGatewayBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterDiscoveredDevices
import com.example.thingsflow.utils.ScanningIoTDeviceCallback
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.CheckDeviceAvailableCallback
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

@AndroidEntryPoint
class FragmentSelectZigbeeGateway : FragmentBase<FragmentSelectZigbeeGatewayBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_select_zigbee_gateway
    private val TAG = "FragmentSelectZigbeeGateway"
    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    private val vmLocation by viewModels<VMLocation>()
    private val gatewayList: ArrayList<IoTDevice> = arrayListOf()
    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDeviceSelected = {uuid, elms ->
                val bundle = bundleOf("gatewayId" to uuid)
                findNavController().navigate(R.id.fragmentIdentifyZigbeeDevice, bundle)
            }
        )
    }
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            rvGateway.adapter = adapterDevices
            adapterDevices.submitList(gatewayList)
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {

            txtCurrentLocation.text = vmLocation.getLocation(vmLocation.getDefaultLocation())?.label

            vmConfigZigbee.checkGatewayAvailable(
                object : CheckDeviceAvailableCallback {
                    override fun onDeviceAvailable(p0: String?) {
                        CoroutineScope(Dispatchers.Main).launch {
                            ILogR.D(TAG, "availableGateway: ", p0)
                            val device = SmartSdk.deviceHandler().get(p0)
                            device?.let {
                                gatewayList.add(device)
                                adapterDevices.notifyDataSetChanged()
                            }
                        }
                    }
                }
            )
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {

        }
    }

}