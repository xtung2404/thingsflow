package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.databinding.DialogDeviceListBinding
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.adapter.AdapterDevices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogDeviceList(
    context: Context,
    private val onDeviceSelected: (String?, IntArray) -> Unit
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
    private var selectedDeviceId: String?= null
    private var selectedElms: IntArray = intArrayOf()
    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDeviceSelected = { devId, elms ->
                selectedDeviceId = devId
                selectedElms = elms
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
                onDeviceSelected.invoke(selectedDeviceId, selectedElms)
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            rvDevice.adapter = adapterDevices
            selectedDeviceId = null
            selectedElms = intArrayOf()

            ILogR.D(TAG, "getList", viewModelOwner, vmDevice?.getAll()?.size, SmartSdk.deviceHandler().all.size)
            vmDevice?.getAll()?.forEach {
                ILogR.D(TAG, "deviceInfo", it?.uuid, it?.label, vmDevice?.getAll()?.size)
            }
            adapterDevices.submitList(
                vmDevice?.getUserDevices()
            )
        }
    }
}