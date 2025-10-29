package com.example.thingsflow.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogConfigWifiBinding
import com.example.thingsflow.databinding.DialogConfigWifiManuallyBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.SuccessRequestCallback

class DialogConfigWiFiManually(
    context: Context,
    private val onConfigSuccess: () -> Unit
): DialogBase<DialogConfigWifiManuallyBinding>(
    context,
    R.layout.dialog_config_wifi_manually
) {
    private val TAG = "DialogConfigWiFi"
    private val vmConfigWileDirect: VMConfigWileDirect? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMConfigWileDirect::class.java]
        }
    }
    override fun setupView(binding: DialogConfigWifiManuallyBinding) {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

            edtSsid.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s?.isEmpty() == true) {
                        btnConnect.isEnabled = false
                        btnConnect.setBackgroundColor(context.getColor(R.color.gray))
                    } else {
                        btnConnect.isEnabled = true
                        btnConnect.setBackgroundDrawable(context.getDrawable(R.drawable.btn_emerald))
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            edtPwd.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (s?.isEmpty() == true) {
                        btnConnect.isEnabled = false
                        btnConnect.setBackgroundColor(context.getColor(R.color.gray))
                    } else {
                        btnConnect.isEnabled = true
                        btnConnect.setBackgroundDrawable(context.getDrawable(R.drawable.btn_emerald))
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            btnConnect.setOnClickListener {
                val ssid = edtSsid.text.toString()
                val pwd = edtPwd.text.toString()
                vmConfigWileDirect?.requestConnectWifiNetwork(
                    ssid,
                    pwd,
                    object : SuccessRequestCallback {
                        override fun onSuccess() {
                            dismiss()
                            onConfigSuccess.invoke()
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            ILogR.D(TAG, "requestConnectWifiNetwork:onFailure ", p0, p1)
                        }
                    }
                )
            }
        }
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            edtSsid.setText("")
            edtPwd.setText("")
            if (edtSsid.text?.isEmpty() == true || edtPwd.text?.isEmpty() == true) {
                btnConnect.isEnabled = false
                btnConnect.setBackgroundColor(context.getColor(R.color.gray))
            }
        }
    }
}