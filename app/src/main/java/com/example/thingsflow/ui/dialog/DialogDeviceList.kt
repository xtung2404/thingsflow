package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeviceListBinding
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.ui.adapter.AdapterDevices
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk

class DialogDeviceList(
    context: Context,
    private val onDeviceSelected: (selectedDevices: HashMap<String?, IntArray>) -> Unit
): DialogBase<DialogDeviceListBinding>(
    context,
    R.layout.dialog_device_list
)  {
    private val TAG = "DialogDeviceList"
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }
    private var selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()
    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDevicesSelected = { devMap ->
                ILogR.D(TAG, "adapterDevices:onDeviceSelected ", devMap.size)
                devMap.forEach {
                    ILogR.D(TAG, "adapterDevices:selectedDeviceInfo ", it.key, it.value)
                }
                selectedDeviceMap = devMap
                binding.btnConfig.isEnabled = devMap.isNotEmpty()
            }
        )
    }

    override fun setupView(binding: DialogDeviceListBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnConfig.setOnClickListener {
                dismiss()
                onDeviceSelected.invoke(selectedDeviceMap)
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            rvDevice.adapter = adapterDevices
            btnConfig.isEnabled = false

            selectedDeviceMap.clear()
            ILogR.D(TAG, "getList", viewModelOwner, vmDevice?.getAll()?.size, SmartSdk.deviceHandler().all.size)
            vmDevice?.getAll()?.forEach {
                ILogR.D(TAG, "deviceInfo", it?.uuid, it?.label, vmDevice?.getAll()?.size)
            }
            adapterDevices.submitList(
                vmDevice?.getUserDevices()?.filter {
                    it.devType != IoTDeviceType.GATEWAY && it.devType != IoTDeviceType.MEDIA_BOX
                }
            )
        }
    }
}