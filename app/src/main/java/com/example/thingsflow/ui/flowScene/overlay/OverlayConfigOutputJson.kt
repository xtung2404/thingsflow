package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import androidx.core.widget.doOnTextChanged
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
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.example.thingsflow.utils.getSupportedBoxType
import com.example.thingsflow.utils.getSupportedDeviceType
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.type.FTypeAction
import rogo.iot.module.flowcommon.type.FTypeEvent
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import kotlin.getValue

class OverlayConfigOutputJson(
    context: Context,
    container: ViewGroup,
    private val onDevicesSelected: (Int?, IntArray?, HashMap<String?, IntArray>) -> Unit,
    private val onClose: () -> Unit
) : OverlayBase<LayoutOverlaySelectDeviceBinding>(
    context,
    container,
    LayoutOverlaySelectDeviceBinding::inflate
) {
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }
    private var selectedDeviceId: String? = null
    private var selectedElms: IntArray = intArrayOf()
    private var selectedDevType: Int = IoTDeviceType.ALL
    private var selectedAttrs: IntArray = intArrayOf()
    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(context, getSupportedDeviceType())
    }
    private val adapterDevices: AdapterDevices by lazy {
        AdapterDevices(
            onDevicesSelected = { devMap ->
            }
        )
    }

    override fun onViewCreated(binding: LayoutOverlaySelectDeviceBinding) {
        binding.apply {
            val devList = vmDevice?.getUserDevices()
            rvDevice.adapter = adapterDevices
            selectedDeviceId = null
            selectedElms = intArrayOf()
            adapterDevices.submitList(devList)
//            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnSave.setOnClickListener {
                val selectedDeviceMap = hashMapOf<String?, IntArray>()
                selectedDeviceMap[selectedDeviceId] = selectedElms
                onDevicesSelected.invoke(
                    selectedDevType,
                    selectedAttrs,
                    selectedDeviceMap
                )
            }

            edtLabel.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val input = s?.toString().orEmpty()

                    val filteredDeviceList = devList?.filter { dev ->
                        // so sánh theo label
                        dev.label.contains(input, ignoreCase = true) ||

                                // so sánh theo devType (chỉ khi input là số)
                                (input.toIntOrNull()?.let { inputNumber ->
                                    getSupportedDeviceType().contains(inputNumber)
                                            && dev.devType == inputNumber
                                } ?: false)
                    }

                    adapterDevices.submitList(filteredDeviceList)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

//            spinnerDeviceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (position != 0) {
////                        val selectedDevType = spinnerDeviceType.selectedItem as Int
//                        val filteredDeviceList = devList?.filter { dev->
//                            dev.devType == selectedDevType
//                        }
//                        adapterDevices.submitList(filteredDeviceList)
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                }
//            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?) {
        super.show()
        binding.apply {
            devType?.let {
                selectedDevType = it
                val devTypePos = adapterSpinnerDeviceType.getPosition(devType)
                if (devTypePos != -1) {
//                    spinnerDeviceType.setSelection(adapterSpinnerDeviceType.getPosition(devType))
                }
            }
            attrs?.let {
                selectedAttrs = it
            }
        }
    }
}