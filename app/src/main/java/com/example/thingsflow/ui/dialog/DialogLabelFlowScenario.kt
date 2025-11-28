package com.example.thingsflow.ui.dialog

import android.content.Context
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogLabelFlowScenarioBinding

class DialogLabelFlowScenario(
    context: Context,
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


        }
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {

        }
    }


    fun show(ssid: String?) {
        super.show()
        binding.apply {

        }
    }
}