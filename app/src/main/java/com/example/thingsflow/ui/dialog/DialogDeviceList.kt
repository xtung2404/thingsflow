package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.databinding.DialogDeviceListBinding
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
    private val viewModelOwner: ViewModelStoreOwner,
    private val onDeviceSelected: (Pair<String?, IntArray>) -> Unit
): DialogBase<DialogDeviceListBinding>(
    context,
    R.layout.dialog_device_list
)  {
    private val TAG = "DialogDeviceList"
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
                onDeviceSelected.invoke(Pair(selectedDeviceId, selectedElms))
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            rvDevice.adapter = adapterDevices
            selectedDeviceId = null
            selectedElms = intArrayOf()
            adapterDevices.submitList(
                SmartSdk.deviceHandler().all.toList()
            )
        }
    }
}