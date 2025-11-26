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
    private val onHeadersConfiged: (ArrayList<ItemHeader>) -> Unit,
    private val onClose: () -> Unit
): DialogBase<DialogConfigHeaderHttpBinding>(
    context,
    R.layout.dialog_config_header_http
)  {
    private val TAG = "DialogConfigHeaderHttp"
    private val headerList = arrayListOf<ItemHeader>()
    private val adapterHeader: AdapterHeader by lazy {
        AdapterHeader(
            onDelete = {pos, header->
                if (pos != 0) {
                    headerList.removeAt(pos)
                    adapterHeader.notifyItemRemoved(pos)
                }
            }
        )
    }

    override fun setupView(binding: DialogConfigHeaderHttpBinding) {
        binding.apply {
            val maxHeight = (context.resources.displayMetrics.heightPixels * 0.3).toInt()
                        rvHeader.viewTreeObserver.addOnGlobalLayoutListener {
                if (rvHeader.height > maxHeight) {
                    rvHeader.layoutParams.height = maxHeight
                    rvHeader.requestLayout()
                }
            }
        }
    }


    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            rvHeader.adapter = adapterHeader
            headerList.clear()
            headerList.add(ItemHeader(key = "", value = ""))
            adapterHeader.submitList(headerList)
            btnCancel.setOnClickListener {
                onClose.invoke()
            }

            btnSave.setOnClickListener {
                onHeadersConfiged.invoke(headerList)
            }

            btnAddHeader.setOnClickListener {
                var isEmpty: Boolean = false
                for (header in headerList) {
                    if (header.key.isEmpty() || header.value.isEmpty()) {
                        isEmpty = true
                        break
                    }
                }

                if (!isEmpty) {
                    headerList.add(ItemHeader(key = "", value = ""))

                    headerList.forEach {
                        ILogR.D(TAG, "headerInfo: ", it.id, it.key, it.value)
                    }

                    adapterHeader.notifyItemInserted(headerList.size - 1)

                    rvHeader.smoothScrollToPosition(headerList.size - 1)
                }
            }
        }
    }
}