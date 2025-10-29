package com.example.thingsflow.ui.dialog

import android.content.Context
import androidx.lifecycle.ViewModelStoreOwner
import com.example.thingsflow.R
import com.example.thingsflow.databinding.DialogSelectConnectivityBinding
import com.example.thingsflow.ui.adapter.AdapterConnectivity
import rogo.iot.module.platform.entity.IoTNetworkConnectivity
import rogo.iot.module.rogocore.sdk.define.IoTConnectivity

class DialogSelectConnectivity(
    context: Context,
    private val onConnectivitySelected: (Int) -> Unit,
    private val onFinish: () -> Unit
): DialogBase<DialogSelectConnectivityBinding>(
    context,
    R.layout.dialog_select_connectivity
)  {
    private val TAG = "DialogSelectConnectivity"
    private var connectivities: HashMap<IoTNetworkConnectivity, Boolean>?= null
    private val adapterConnectivity: AdapterConnectivity by lazy {
        AdapterConnectivity(
            onItemClick = {infType ->
                dismiss()
                onConnectivitySelected.invoke(infType)
            }
        )
    }

    override fun setupView(binding: DialogSelectConnectivityBinding) {
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }

            btnContinue.setOnClickListener {
                onFinish.invoke()
            }
        }
    }

    override fun onDialogShown() {
        super.onDialogShown()
        binding.apply {
            binding.rvConnectivity.adapter = adapterConnectivity
            adapterConnectivity.submitList(connectivities?.entries?.toList())
        }
    }

    fun show(connectivities: HashMap<IoTNetworkConnectivity, Boolean>) {
        this.connectivities = connectivities
        super.show()

    }
}