package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.databinding.DialogDeviceListBinding
import com.example.thingsflow.databinding.DialogDeviceTypeBinding
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.adapter.AdapterDeviceType
import com.example.thingsflow.ui.adapter.AdapterDevices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.platform.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogDeviceType(
    context: Context,
    private val onDeviceTypeSelected: (Int) -> Unit
): DialogBase<DialogDeviceTypeBinding>(
    context,
    R.layout.dialog_device_type
)  {
    private val TAG = "DialogDeviceType"

    override fun setupView(binding: DialogDeviceTypeBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            btnGateway.setOnClickListener {
                onDeviceTypeSelected.invoke(IoTDeviceType.GATEWAY)
                dismiss()
            }

            btnOtherDevice.setOnClickListener {
                onDeviceTypeSelected.invoke(IoTDeviceType.OTHER)
                dismiss()
            }
        }
    }
}