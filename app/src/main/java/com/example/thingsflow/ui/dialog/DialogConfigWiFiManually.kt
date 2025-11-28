package com.example.thingsflow.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogConfigWifiManuallyBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.callback.RequestStatusCallback

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
                    object : RequestStatusCallback {
                        override fun onSuccess() {
                            dismiss()
                            onConfigSuccess.invoke()
                        }

                        override fun onError(p0: Int) {
                            ILogR.D(TAG, "requestConnectWifiNetwork:onFailure ", p0)
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