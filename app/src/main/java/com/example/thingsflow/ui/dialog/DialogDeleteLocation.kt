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
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogDeleteLocation(
    context: Context,
    private val onCancel: (IoTLocation) -> Unit
): DialogBase<DialogDeleteLocationBinding>(
    context,
    R.layout.dialog_delete_location
)  {
    private val TAG = "DialogDeleteLocation"
    private var ioTLocation: IoTLocation?= null
    private val vmLocation: VMLocation? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMLocation::class.java]
        }
    }
    override fun setupView(binding: DialogDeleteLocationBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                ioTLocation?.let(onCancel)
            }
            btnDeleteLocation.setOnClickListener {
                ioTLocation?.let {
                    vmLocation?.delete(
                        it.uuid,
                        object: RequestResultCallback<Boolean> {
                            override fun onResult(p0: Boolean?) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    vmLocation?.refresh()
                                    dismiss()
                                }
                            }

                            override fun onError(p0: Int) {
                                ILogR.D(TAG, "ON_DELETE:onFailure", p0)
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
//            txtLabel.text = ioTLocation?.label
        }
    }

    fun show(ioTLocation: IoTLocation) {
        this.ioTLocation = ioTLocation
        super.show()
    }
}