package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogConfigHeaderHttpBinding
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.databinding.DialogDeviceListBinding
import com.example.thingsflow.module.model.ItemHeader
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.ui.adapter.AdapterHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogConfigHeaderHttp(
    context: Context,
    private val onDeviceSelected: (String?, IntArray) -> Unit
): DialogBase<DialogConfigHeaderHttpBinding>(
    context,
    R.layout.dialog_config_header_http
)  {
    private val TAG = "DialogConfigHeaderHttp"
    private val headerList = arrayListOf<ItemHeader>()
    private val adapterHeader: AdapterHeader by lazy {
        AdapterHeader()
    }

    override fun setupView(binding: DialogConfigHeaderHttpBinding) {
        binding.apply {

        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            headerList.clear()
            rvHeader.adapter = adapterHeader
            headerList.add(ItemHeader(key = "", value = ""))
            adapterHeader.submitList(headerList)
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnCancel.setOnClickListener {
                headerList.add(if (headerList.size == 1) 1 else headerList.size - 1, ItemHeader(key = "", value = ""))
                adapterHeader.notifyItemInserted(headerList.size - 1)
            }

            btnSave.setOnClickListener {

            }
        }
    }
}