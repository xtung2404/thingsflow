package com.example.thingsflow.ui.dialog

import android.content.Context
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogAddAndEditJsonFieldBinding
import com.example.thingsflow.ui.adapter.AdapterSpinnerPrimitiveType
import rogo.iot.module.flowcommon.type.FInputValueType

class DialogAddAndEditJsonField(
    context: Context,
    private var onNewFieldConfigured: (key: String, type: Int) -> Unit
): DialogBase<DialogAddAndEditJsonFieldBinding>(
    context,
    R.layout.dialog_add_and_edit_json_field
) {

    fun setCallback(callback: (String, Int) -> Unit) {
        this.onNewFieldConfigured = callback
    }
    private val adapterSpinnerPrimitiveType: AdapterSpinnerPrimitiveType by lazy {
        AdapterSpinnerPrimitiveType(context, listOf(
            FInputValueType.INTEGER,
            FInputValueType.FLOAT,
            FInputValueType.STRING,
            FInputValueType.OBJECT
        ))
    }
    override fun setupView(binding: DialogAddAndEditJsonFieldBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnSave.setOnClickListener {
                val key = edtKey.text.toString()
                val type = spinnerType.selectedItem as Int
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