package com.example.thingsflow.ui.device

import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentDeviceDetailBinding
import com.example.thingsflow.ui.FragmentBase


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
//                SmartSdk.featureHandler().runFeatureMethod(
//                    false,
//                    deviceUUID,
//                    IoTFeature.BUILTIN_SERVICE_FLOW,
//                    "getWorld",
//                    object : IoTInvokingParam {
//
//                    },
//                    30000,
//                    object : FeatureMethodRequestResultCallback {
//                        override fun onResult(p0: Int, p1: String?) {
//                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(requireContext(), "getSuccess", Toast.LENGTH_LONG).show()
//                            }
//                        }
//
//                        override fun onError(p0: Int) {
//                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(requireContext(), "onFailure $p1", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    }
//                )
            }

            btnSet.setOnClickListener {
//                SmartSdk.featureHandler().runFeatureMethod(
//                    true,
//                    deviceUUID,
//                    IoTFeature.BUILTIN_SERVICE_FLOW,
//                    "setWorld",
//                    object : IoTInvokingParam {
//                        @IoTInvokingProperty("label")
//                        private var label: String = edtInput.text.toString()
//
//                    },
//                    30000,
//                    object : FeatureMethodRequestResultCallback {
//                        override fun onResult(p0: Int, p1: String?) {
//                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(requireContext(), "setSuccess", Toast.LENGTH_LONG).show()
//                            }
//                        }
//
//                        override fun onError(p0: Int) {
//                            CoroutineScope(Dispatchers.Main).launch {
//                                Toast.makeText(requireContext(), "setFailure $p1", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    }
//                )
            }
        }
    }

}