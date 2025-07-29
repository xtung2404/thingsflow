package com.example.thingsflow.ui.deviceConfig

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSetDeviceLabelBinding
import com.example.thingsflow.module.viewmodel.ConfigWileDirectViewModel
import com.example.thingsflow.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

@AndroidEntryPoint
class SetDeviceLabelFragment : BaseFragment<FragmentSetDeviceLabelBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_set_device_label

    private var identifiedDevice: IoTDirectDeviceInfo?= null
    private var selectedGroup: String?= null
    private val configWileDirectViewModel by activityViewModels<ConfigWileDirectViewModel>()
    override fun initVariable() {
        super.initVariable()
        identifiedDevice = configWileDirectViewModel.getIdentifiedDevice()
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

                configWileDirectViewModel.setupAndSyncDeviceToCloud(
                    edtLabel.text.toString(),
                    selectedGroup,
                    SmartSdk.getProductModel(identifiedDevice?.productId).devSubType,
                    object : RequestCallback<IoTDevice> {
                        override fun onSuccess(p0: IoTDevice?) {

                        }

                        override fun onFailure(p0: Int, p1: String?) {

                        }
                    }
                )
            }
        }
    }
}