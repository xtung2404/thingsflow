package com.example.thingsflow.ui.deviceConfig.zigbee

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSelectZigbeeGatewayBinding
import com.example.thingsflow.module.viewmodel.VMConfigZigbee
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterGateway
import com.example.thingsflow.ui.adapter.AdapterSpinnerLocation
import com.example.thingsflow.ui.dialog.showDialogLoadingWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

@AndroidEntryPoint
class FragmentSelectZigbeeGateway : FragmentBase<FragmentSelectZigbeeGatewayBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_select_zigbee_gateway
    private val TAG = "FragmentSelectZigbeeGateway"
    private val vmConfigZigbee by activityViewModels<VMConfigZigbee>()
    private val vmLocation by viewModels<VMLocation>()
    private var gatewayList: ArrayList<IoTDevice> = arrayListOf()
    private var selectedGateway: String? = null
    private val adapterGateway: AdapterGateway by lazy {
        AdapterGateway(
            onDeviceSelected = { uuid, elms ->
                selectedGateway = uuid
            }
        )
    }
    private lateinit var adapterSpinnerLocation: AdapterSpinnerLocation
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            gatewayList.clear()
            gatewayList.addAll(SmartSdk.deviceHandler().userDevices.filter {
                it.devType == IoTDeviceType.GATEWAY
//                        ||
//                        it.devType == IoTDeviceType.IR_DEVICE_CONTROLLER
            })
            rvGateway.adapter = adapterGateway
            adapterGateway.submitList(gatewayList)
            vmLocation.refresh()
            vmLocation.locationsLiveData.observe(
                requireActivity()
            ) {
                adapterSpinnerLocation = AdapterSpinnerLocation(
                    requireContext(),
                    it
                )
                spinnerLocation.adapter = adapterSpinnerLocation
            }
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLocation = parent?.getItemAtPosition(position) as IoTLocation?
                    val filteredGateway = gatewayList.filter {
                        it.locationId.contentEquals(selectedLocation?.uuid)
                    }.toMutableList()
                    adapterGateway.submitList(filteredGateway)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnContinue.setOnClickListener {
                val loadingDialog = requireContext().showDialogLoadingWithAnimation(
                    R.string.checking_on_gateway,
                    R.string.wait_a_little,
                    lifecycle = lifecycle
                )
                loadingDialog.show()
                val selectedLocation = spinnerLocation.selectedItem as IoTLocation
                if (vmLocation.getDefaultLocation()
                        ?.contentEquals(selectedLocation.uuid) == false
                ) {
                    vmLocation.setDefaultLocation(selectedLocation.uuid)
                }
                ILogR.D(TAG, "selectZigbeeGateway:defaultLocation", vmLocation.getDefaultLocation(),
                    FlowSdk.locationHandler().get(vmLocation.getDefaultLocation()).label
                )
                selectedGateway?.let {
                    vmConfigZigbee.isGatewayAvailable(
                        it,
                        8,
                        object : RequestResultCallback<Boolean> {
                            override fun onResult(p0: Boolean?) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    loadingDialog.dismiss()
                                    if (p0 == true) {
                                        ILogR.D(TAG, "isGatewayAvailabel:onSuccess")
                                        val bundle: Bundle = bundleOf("gatewayId" to it)
                                        findNavController().navigate(R.id.fragmentIdentifyZigbeeDevice, bundle)
                                    } else {
                                        ILogR.D(TAG, "isGatewayAvailabel:onFailure")
                                    }
                                }
                            }

                            override fun onError(p0: Int) {
                                loadingDialog.dismiss()
                                ILogR.D(TAG, "isGatewayAvailabel:onFailure", p0)
                            }
                        }
                    )
                }
            }
        }
    }

}