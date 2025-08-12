package com.example.thingsflow.ui.dialog

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogConfigWifiBinding
import com.example.thingsflow.module.viewmodel.VMConfigWileDirect
import rogo.iot.module.platform.ILogR
import rogo.iot.module.platform.callback.SuccessRequestCallback

class DialogConfigWiFi(
    context: Context,
    private val viewModelOwner: ViewModelStoreOwner,
    private val onConfigSuccess: () -> Unit
): BaseDialog<DialogConfigWifiBinding>(
    context,
    R.layout.dialog_config_wifi
) {
    private val TAG = "DialogConfigWiFi"
    private val vmConfigWileDirect: VMConfigWileDirect by lazy {
        ViewModelProvider(viewModelOwner)[VMConfigWileDirect::class.java]
    }
    override fun setupView(binding: DialogConfigWifiBinding) {
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                dismiss()
            }

            btnConnect.setOnClickListener {
                val ssid = edtSsid.text.toString()
                val pwd = edtPwd.text.toString()
                vmConfigWileDirect.requestConnectWifiNetwork(
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
        }
    }


    fun show(ssid: String?) {
        super.show()
        binding.apply {
            edtSsid.visibility = if (ssid == null) View.VISIBLE else View.GONE
            ssid?.let {
                edtSsid.setText(it)
            }
            edtPwd.setText("")
        }
    }
}