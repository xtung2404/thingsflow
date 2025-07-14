package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogDeleteLocationBinding
import com.example.thingsflow.module.viewmodel.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogDeleteLocation(
    context: Context,
    private val viewModelOwner: ViewModelStoreOwner,
    private val onCancel: (IoTLocation) -> Unit
): BaseDialog<DialogDeleteLocationBinding>(
    context,
    R.layout.dialog_delete_location
)  {
    private val TAG = "DialogDeleteLocation"
    private var ioTLocation: IoTLocation?= null
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(viewModelOwner)[LocationViewModel::class.java]
    }
    override fun setupView(binding: DialogDeleteLocationBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                ioTLocation?.let(onCancel)
            }
            btnDeleteLocation.setOnClickListener {
                ioTLocation?.let {
                    locationViewModel.delete(
                        it.uuid,
                        object: RequestCallback<Boolean> {
                            override fun onSuccess(p0: Boolean?) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    locationViewModel.refresh()
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