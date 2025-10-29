package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogCreateGroupBinding
import com.example.thingsflow.module.viewmodel.VMGroup
import com.example.thingsflow.ui.adapter.AdapterSpinnerLocationType
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

class DialogCreateGroup(
    context: Context,
): DialogBase<DialogCreateGroupBinding>(
    context,
    R.layout.dialog_create_group
) {
    private val vmGroup: VMGroup? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMGroup::class.java]
        }
    }


    override fun setupView(binding: DialogCreateGroupBinding) {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                dismiss()
            }
            btnSave.setOnClickListener {
                val label = edtLabel.text.toString()
                if (label.isNotEmpty()) {
                    vmGroup?.create(
                        edtLabel.text.toString(),
                        spinnerGroupType.selectedItem as String,
                        object : RequestCallback<IoTGroup> {
                            override fun onSuccess(p0: IoTGroup?) {
                                dismiss()
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
            edtLabel.setText("")
            val locationTypeSpinnerAdapter = AdapterSpinnerLocationType(
                context,
                context.resources.getStringArray(R.array.location_type).toList()
            )
            spinnerGroupType.adapter = locationTypeSpinnerAdapter
        }
    }

}