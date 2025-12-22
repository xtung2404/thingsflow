package com.example.thingsflow.ui.dialog

import android.content.Context
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogLabelFlowScenarioBinding

class DialogLabelFlowScenario(
    context: Context,
    private val onLabelChanged: (String) -> Unit
): DialogBase<DialogLabelFlowScenarioBinding>(
    context,
    R.layout.dialog_label_flow_scenario
) {
    private val TAG = "DialogConfigWiFi"
    override fun setupView(binding: DialogLabelFlowScenarioBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnClose.setOnClickListener {
                dismiss()
            }


            btnSave.setOnClickListener {
                val label = edtLabel.text.toString()
                onLabelChanged.invoke(label)
                dismiss()
            }
        }
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {

        }
    }


    fun show(label: String?) {
        super.show()
        binding.apply {
            edtLabel.setText(label)
        }
    }
}