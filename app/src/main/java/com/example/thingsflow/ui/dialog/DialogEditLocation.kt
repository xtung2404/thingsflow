package com.example.thingsflow.ui.dialog

import android.content.Context
import android.nfc.Tag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogEditLocationBinding
import com.example.thingsflow.module.viewmodel.VMLocation
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogEditLocation(
    context: Context,
    private val onDeleteLoc:(IoTLocation) -> Unit
): DialogBase<DialogEditLocationBinding>(
    context,
    R.layout.dialog_edit_location
) {
    private var ioTLocation: IoTLocation?= null
    private val TAG = "DialogEditLocation"
    private val vmLocation: VMLocation? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMLocation::class.java]
        }
    }
    override fun setupView(binding: DialogEditLocationBinding) {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                dismiss()
            }
            btnDeleteLocation.setOnClickListener {
                ioTLocation?.let {
                    onDeleteLoc.invoke(it)
                }
            }

            btnSave.setOnClickListener {
                ioTLocation?.let {
                    ILogR.D(TAG, "locationInfo:", Gson().toJson(it))
                    ILogR.D(TAG, "locationViewModel:", vmLocation == null)
                    vmLocation?.update(
                        it,
                        edtLabel.text.toString(),
                        object : RequestResultCallback<IoTLocation> {
                            override fun onResult(p0: IoTLocation?) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    vmLocation?.refresh()
                                    dismiss()
                                }
                            }

                            override fun onError(p0: Int) {

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
            edtLabel.setText(ioTLocation?.label)
        }
    }

    fun show(
        ioTLocation: IoTLocation
    ) {
        this.ioTLocation = ioTLocation
        super.show()
    }

}