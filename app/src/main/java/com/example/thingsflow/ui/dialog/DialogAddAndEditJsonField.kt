package com.example.thingsflow.ui.dialog

import android.content.Context
import android.nfc.Tag
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogAddAndEditJsonFieldBinding
import com.example.thingsflow.databinding.DialogEditLocationBinding
import com.example.thingsflow.module.define.TFPrimitiveType
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.adapter.AdapterSpinnerPrimitiveType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class DialogAddAndEditJsonField(
    context: Context,
    private var onNewFieldConfigured: (key: String, type: TFPrimitiveType) -> Unit
): DialogBase<DialogAddAndEditJsonFieldBinding>(
    context,
    R.layout.dialog_add_and_edit_json_field
) {

    fun setCallback(callback: (String, TFPrimitiveType) -> Unit) {
        this.onNewFieldConfigured = callback
    }
    private val adapterSpinnerPrimitiveType: AdapterSpinnerPrimitiveType by lazy {
        AdapterSpinnerPrimitiveType(context, listOf(
            TFPrimitiveType.INT,
            TFPrimitiveType.LONG,
            TFPrimitiveType.STRING,
            TFPrimitiveType.OBJECT
        ))
    }
    override fun setupView(binding: DialogAddAndEditJsonFieldBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnSave.setOnClickListener {
                val key = edtKey.text.toString()
                val type = spinnerType.selectedItem as TFPrimitiveType
                onNewFieldConfigured.invoke(key, type)
            }
        }
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            spinnerType.adapter = adapterSpinnerPrimitiveType
            edtKey.setText("")
        }
    }
}