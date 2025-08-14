package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.module.viewmodel.VMLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogDeviceList(
    context: Context,
    private val viewModelOwner: ViewModelStoreOwner,
    private val onCancel: (IoTLocation) -> Unit
): DialogBase<DialogDeleteLocationBinding>(
    context,
    R.layout.dialog_delete_location
)  {
    private val TAG = "DialogDeleteLocation"
    private var ioTLocation: IoTLocation?= null
    private val vmLocation: VMLocation by lazy {
        ViewModelProvider(viewModelOwner)[VMLocation::class.java]
    }
    override fun setupView(binding: DialogDeleteLocationBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                ioTLocation?.let(onCancel)
            }
            btnDeleteLocation.setOnClickListener {
                ioTLocation?.let {
                    vmLocation.delete(
                        it.uuid,
                        object: RequestCallback<Boolean> {
                            override fun onSuccess(p0: Boolean?) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    vmLocation.refresh()
                                    dismiss()
                                }
                            }

                            override fun onFailure(p0: Int, p1: String?) {
                                ILogR.D(TAG, "ON_DELETE:onFailure", p0, p1)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            txtLabel.text = ioTLocation?.label
        }
    }

    fun show(ioTLocation: IoTLocation) {
        this.ioTLocation = ioTLocation
        super.show()
    }
}