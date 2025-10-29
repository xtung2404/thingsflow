package com.example.thingsflow.ui.deviceConfig.gateway

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentConfigGatewayBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import com.example.thingsflow.module.viewmodel.VMGroup
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterSpinnerGroup
import com.example.thingsflow.ui.dialog.DialogCreateGroup
import com.example.thingsflow.ui.dialog.showDialogLoadingWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

@AndroidEntryPoint
class FragmentConfigGateway : FragmentBase<FragmentConfigGatewayBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_config_gateway

    private val TAG = "FragmentSetDeviceLabel"
    private var identifiedDevice: IoTDirectDeviceInfo?= null
    private var selectedGroup: String?= null
    private val vmConfigWileDirect by activityViewModels<VMConfigWileDirect>()
    private val vmGroup by activityViewModels<VMGroup>()
    private lateinit var adapterSpinnerGroup: AdapterSpinnerGroup
    private val dialogCreateGroup: DialogCreateGroup by lazy {
        DialogCreateGroup(
            requireContext()
        )
    }
    override fun initVariable() {
        super.initVariable()
        identifiedDevice = vmConfigWileDirect.getIdentifiedDevice()
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = resources.getString(R.string.connect_to_wifi)
            ILogR.D(TAG, "deviceInfo:", identifiedDevice?.productId, SmartSdk.getProductModel(identifiedDevice?.productId).name)
            edtLabel.setText(SmartSdk.getProductModel(identifiedDevice?.productId).name)
            val availableGroups = arrayListOf<IoTGroup?>()
            availableGroups.add(null)
            availableGroups.addAll(vmGroup.getAll())
            adapterSpinnerGroup = AdapterSpinnerGroup(
                requireContext(),
                availableGroups.toList()
            )
            spinnerGroup.adapter = adapterSpinnerGroup
        }
    }
    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.configWiFiFragment)
            }
            btnFinish.setOnClickListener {
                val dialogLoading = context?.showDialogLoadingWithAnimation(
                    R.string.config,
                    R.string.config,
                    lifecycle = lifecycle
                )
                dialogLoading?.show()
                vmConfigWileDirect.setupAndSyncDeviceToCloud(
                    edtLabel.text.toString(),
                    selectedGroup,
                    SmartSdk.getProductModel(identifiedDevice?.productId).devSubType,
                    object : RequestCallback<IoTDevice> {
                        override fun onSuccess(p0: IoTDevice?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                dialogLoading?.dismiss()
                                findNavController().navigate(R.id.fragmentDevice)
                            }
                        }

                        override fun onFailure(p0: Int, p1: String?) {

                        }
                    }
                )
            }

            btnAddAnotherGateway.setOnClickListener {

            }
        }
    }
}