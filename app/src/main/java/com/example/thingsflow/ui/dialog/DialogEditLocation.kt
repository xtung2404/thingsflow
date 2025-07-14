package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogEditLocationBinding
import com.example.thingsflow.module.viewmodel.LocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogEditLocation(
    context: Context,
    private val viewModelOwner: ViewModelStoreOwner,
    private val onDeleteLoc:(IoTLocation) -> Unit
): BaseDialog<DialogEditLocationBinding>(
    context,
    R.layout.dialog_edit_location
) {
    private var ioTLocation: IoTLocation?= null
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProvider(viewModelOwner)[LocationViewModel::class.java]
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
                    locationViewModel.update(
                        it,
                        edtLabel.text.toString(),
                        object : RequestCallback<IoTLocation> {
                            override fun onSuccess(p0: IoTLocation?) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    locationViewModel.refresh()
                                    dismiss()
                                }
                            }

                            override fun onFailure(p0: Int, p1: String?) {

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