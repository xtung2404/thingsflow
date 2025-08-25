package com.example.thingsflow.ui.deviceConfig

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSetDeviceLabelBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.ui.FragmentBase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

@AndroidEntryPoint
class FragmentSetDeviceLabel : FragmentBase<FragmentSetDeviceLabelBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_set_device_label

    private var identifiedDevice: IoTDirectDeviceInfo?= null
    private var selectedGroup: String?= null
    private val vmConfigWileDirect by activityViewModels<VMConfigWileDirect>()
    override fun initVariable() {
        super.initVariable()
        identifiedDevice = vmConfigWileDirect.getIdentifiedDevice()
        arguments?.let {
            selectedGroup = it.getString("group")
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            edtLabel.setText(SmartSdk.getProductModel(identifiedDevice?.productId).name)
        }
    }
    override fun initAction() {
        super.initAction()
        binding.apply {
            btnFinish.setOnClickListener {

                vmConfigWileDirect.setupAndSyncDeviceToCloud(
                    edtLabel.text.toString(),
                    selectedGroup,
                    SmartSdk.getProductModel(identifiedDevice?.productId).devSubType,
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