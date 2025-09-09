package com.example.thingsflow.ui.deviceConfig.zigbee

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSetDeviceLabelBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.ui.FragmentBase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

@AndroidEntryPoint
class FragmentSetZigbeeDeviceLabel : FragmentBase<FragmentSetDeviceLabelBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_set_device_label
    private var gatewayId: String?= null
    private var ioTPairedZigbeeDevice: IoTPairedZigbeeDevice?= null

    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    override fun initVariable() {
        super.initVariable()
        arguments?.let {
            gatewayId = it.getString("gatewayId")
            ioTPairedZigbeeDevice = it.getParcelable("pairedDevice")
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            ioTPairedZigbeeDevice?.let {
                edtLabel.setText(it.ioTProductModel.name)
            }
        }
    }
    override fun initAction() {
        super.initAction()
        binding.apply {
            btnFinish.setOnClickListener {
                vmConfigZigbee.syncDeviceToCloud(
                    gatewayId!!,
                    ioTPairedZigbeeDevice!!,
                    edtLabel.text.toString(),
                    null,
                    ioTPairedZigbeeDevice!!.ioTProductModel.devSubType,
                    object : RequestCallback<IoTDevice> {
                        override fun onSuccess(p0: IoTDevice?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                findNavController().navigate(R.id.homeFragment)
                            }
                        }

                        override fun onFailure(p0: Int, p1: String?) {

                        }
                    }
                )
            }
        }
    }
}