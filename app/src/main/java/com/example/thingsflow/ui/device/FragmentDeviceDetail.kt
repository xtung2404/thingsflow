package com.example.thingsflow.ui.device

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentDeviceDetailBinding
import com.example.thingsflow.ui.FragmentBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.feature.IoTFeature
import rogo.iot.module.platform.invoking.IoTInvokingParam
import rogo.iot.module.platform.invoking.IoTInvokingProperty
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.FeatureMethodRequestCallback


class FragmentDeviceDetail : FragmentBase<FragmentDeviceDetailBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_device_detail

    private var deviceUUID: String?= null
    override fun initVariable() {
        super.initVariable()
        arguments?.let {
            deviceUUID = it.getString("device")
        }
    }

    override fun initView() {
        super.initView()

    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnGet.setOnClickListener {
                SmartSdk.featureHandler().runFeatureMethod(
                    false,
                    deviceUUID,
                    IoTFeature.BUILTIN_SERVICE_FLOW,
                    "getWorld",
                    object : IoTInvokingParam {

                    },
                    30000,
                    object : FeatureMethodRequestCallback {
                        override fun onSuccess(p0: Int, p1: String?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(requireContext(), "getSuccess", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(requireContext(), "onFailure $p1", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }

            btnSet.setOnClickListener {
                SmartSdk.featureHandler().runFeatureMethod(
                    true,
                    deviceUUID,
                    IoTFeature.BUILTIN_SERVICE_FLOW,
                    "setWorld",
                    object : IoTInvokingParam {
                        @IoTInvokingProperty("label")
                        private var label: String = edtInput.text.toString()

                    },
                    30000,
                    object : FeatureMethodRequestCallback {
                        override fun onSuccess(p0: Int, p1: String?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(requireContext(), "setSuccess", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(requireContext(), "setFailure $p1", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        }
    }

}