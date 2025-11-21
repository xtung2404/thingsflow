package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxActionTypeBinding
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxEventTypeBinding
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxTypeBinding
import com.example.thingsflow.databinding.LayoutOverlaySelectDeviceBinding
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxActionType
import com.example.thingsflow.ui.adapter.AdapterBoxEventType
import com.example.thingsflow.ui.adapter.AdapterBoxType
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.example.thingsflow.utils.getSupportedBoxType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.type.FTypeAction
import rogo.iot.module.flowcommon.type.FTypeEvent
import rogo.iot.module.platform.ILogR
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import kotlin.getValue

class OverlaySelectDevice(
    context: Context,
    container: ViewGroup,
    private val onDevicesSelected: (ArrayList<IoTDevice>) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlaySelectDeviceBinding>(
    context,
    container,
    LayoutOverlaySelectDeviceBinding::inflate
) {
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }
    private var selectedDeviceId: String?= null
    private var selectedElms: IntArray = intArrayOf()
    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDeviceSelected = { devId, elms ->
                selectedDeviceId = devId
                selectedElms = elms
            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlaySelectDeviceBinding) {
        binding.apply {


            btnBack.setOnClickListener {
                onClose.invoke()
            }

            rvDevice.adapter = adapterDevices
            selectedDeviceId = null
            selectedElms = intArrayOf()

            adapterDevices.submitList(
                vmDevice?.getUserDevices()
            )

        }
    }
}