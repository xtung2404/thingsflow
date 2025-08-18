package com.example.thingsflow.ui.dialog

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogConfigWifiBinding
import com.example.thingsflow.databinding.DialogLabelFlowScenarioBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.SuccessRequestCallback

class DialogLabelFlowScenario(
    context: Context,
    private val viewModelOwner: ViewModelStoreOwner
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