package com.example.thingsflow.ui.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogConfigWifiBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.SuccessRequestCallback

class DialogConfigWiFi(
    context: Context,
    private val onConfigSuccess: () -> Unit
): DialogBase<DialogConfigWifiBinding>(
    context,
    R.layout.dialog_config_wifi
) {
    private var ssid: String?= null
    private val TAG = "DialogConfigWiFi"
    private val vmConfigWileDirect: VMConfigWileDirect? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMConfigWileDirect::class.java]
        }
    }
    override fun setupView(binding: DialogConfigWifiBinding) {
        binding.apply {
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
                val pwd = edtPwd.text.toString()
                ssid?.let {
                    vmConfigWileDirect?.requestConnectWifiNetwork(
                        it,
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
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            txtSsid.text = ssid
        }
    }


    fun show(ssid: String?) {
        this.ssid = ssid
        super.show()
        binding.apply {
            edtPwd.setText("")
            if (edtPwd.text?.isEmpty() == true) {
                btnConnect.isEnabled = false
                btnConnect.setBackgroundColor(context.getColor(R.color.gray))
            }
        }

    }
}