package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlaySelectDeviceBinding
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterDevices
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.getSupportedDeviceType
import rogo.iot.module.platform.define.IoTDeviceType
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

/**
 * @file: This overlay is used to select devices
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onDevicesSelected: triggered when devices are selected
 * @param onClose: triggered when hide the overlay
 */
class OverlaySelectDevice(
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

    // selectedDeviceId is to store uuid of selected device
    private var selectedDeviceId: String? = null

    // selectedElms is to store selected elements of selected devices
    private var selectedElms: IntArray = intArrayOf()

    // selectedElms is to store selected type of device
    private var selectedDevType: Int = IoTDeviceType.ALL

    // selectedElms is to store selected attributes
    private var selectedAttrs: IntArray = intArrayOf()

    private val originalDevList: MutableList<IoTDevice> = mutableListOf()
    // adapter for select attributes
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
            rvDevice.adapter = adapterDevices

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

            edtLabel.doOnTextChanged { text, _, _, _ ->
                filterAndDisplayDevices(text?.toString().orEmpty())
            }
        }
    }

    private fun filterAndDisplayDevices(query: String) {
        val filteredList = if (query.isBlank()) {
            originalDevList
        } else {
            val queryAsInt = query.toIntOrNull()
            val supportedDeviceTypes by lazy { getSupportedDeviceType() }

            originalDevList.filter { dev ->
                // Compare by label
                dev.label?.contains(query, ignoreCase = true) == true ||
                        // Compare by devType (only if input is a number and a supported type)
                        (queryAsInt != null && supportedDeviceTypes.contains(queryAsInt) && dev.devType == queryAsInt)
            }
        }
        adapterDevices.submitList(filteredList)
    }


    fun show(devType: Int?, attrs: IntArray?) {
        super.show()
        binding.apply {
            originalDevList.clear()
            selectedDeviceId = null
            selectedElms = intArrayOf()

            selectedDevType = devType?: IoTDeviceType.ALL
            selectedAttrs = attrs ?: intArrayOf()

            txtDevType.text = getDeviceTypeLabel(context, selectedDevType)
            txtAttr.text = if (selectedAttrs.isNotEmpty()) getAttrLabel(context, selectedAttrs[0]) else ""
            if (selectedAttrs.isNotEmpty()) {
                txtAttr.text = getAttrLabel(context, selectedAttrs[0])
            }
            val filteredDevices = vmDevice?.getUserDevices()
                ?.asSequence() // Use sequence for better performance on large lists
                ?.filter { dev ->
                    (selectedDevType == IoTDeviceType.ALL || dev.devType == selectedDevType) &&
                            (selectedAttrs.isEmpty() || dev.features?.any { it in selectedAttrs } == true)
                }
                ?.distinct() // Ensure unique devices if they match multiple attributes
                ?.toList() ?: emptyList()

            originalDevList.addAll(filteredDevices)
            adapterDevices.submitList(originalDevList)
        }
    }
}