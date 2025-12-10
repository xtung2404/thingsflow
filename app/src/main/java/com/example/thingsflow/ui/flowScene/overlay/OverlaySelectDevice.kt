package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
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
import rogo.iot.module.base.ILogR
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.flowcommon.type.FBoxType
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
    private val TAG = "OverlaySelectDevice"
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }

    private var selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    private var selectedDevType: Int = IoTDeviceType.ALL

    // selectedElms is to store selected attributes
    private var selectedAttrs: IntArray = intArrayOf()

    private val originalDevList: MutableList<IoTDevice> = mutableListOf()

    // adapter for select attributes
    private lateinit var adapterDevices: AdapterDevices

    override fun onViewCreated(binding: LayoutOverlaySelectDeviceBinding) {
        binding.apply {

        }
    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            edtLabel.doOnTextChanged { text, _, _, _ ->
                filterAndDisplayDevices(text?.toString().orEmpty())
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnSave.setOnClickListener {
                onDevicesSelected.invoke(
                    selectedDevType,
                    selectedAttrs,
                    selectedDeviceMap
                )
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
                        (queryAsInt != null && supportedDeviceTypes.contains(queryAsInt) && if (selectedDevType == IoTDeviceType.ALL) true else dev.devType == selectedDevType)
            }
        }
        adapterDevices.submitList(filteredList)
    }

    fun show(
        currentBoxType: Int?,
        devType: Int?,
        attrs: IntArray?
    ) {
        selectedDeviceMap.clear()
        originalDevList.clear()

        super.show()
        binding.apply {
            initialize(devType, attrs)
            showUIDevices(currentBoxType == FBoxType.ACT_CONTROL_DEVICE)
        }
    }

    fun show(
        currentBoxType: Int?,
        devType: Int?,
        attrs: IntArray?,
        devMap: HashMap<String?, IntArray>?= null
    ) {
        super.show()

        selectedDeviceMap = devMap?: hashMapOf()
        originalDevList.clear()
        binding.apply {
            initialize(devType, attrs)
            showUIDevices(currentBoxType == FBoxType.ACT_CONTROL_DEVICE)
        }
    }

    private fun initialize(devType: Int?, attrs: IntArray?) {
        binding.apply {
            selectedDevType = devType ?: IoTDeviceType.ALL
            selectedAttrs = attrs ?: intArrayOf()

            txtDevType.text = getDeviceTypeLabel(context, selectedDevType)

            txtAttr.text =
                if (selectedAttrs.isNotEmpty()) getAttrLabel(context, selectedAttrs[0]) else ""

            if (selectedAttrs.isNotEmpty()) {
                txtAttr.text = getAttrLabel(context, selectedAttrs[0])
            }
        }
    }

    private fun showUIDevices(isAllowedToSelectMultipleDevices: Boolean) {
        binding.apply {
            adapterDevices = AdapterDevices(
                isAllowedToSelectMultipleDevices = isAllowedToSelectMultipleDevices,
                onDevicesSelected = { devMap ->
                    selectedDeviceMap = devMap
                }
            )
            rvDevice.adapter = adapterDevices
            val filteredDevices = vmDevice?.getUserDevices()
                ?.asSequence() // Use sequence for better performance on large lists
                ?.filter { dev ->
                    if (selectedDevType != IoTDeviceType.ALL) {
                        dev.devType == selectedDevType
                    } else {
                        true
                    }
                }
                ?.distinct() // Ensure unique devices if they match multiple attributes
                ?.toList() ?: emptyList()

            originalDevList.addAll(filteredDevices)
            adapterDevices.submitList(originalDevList)
            adapterDevices.setSelectedDeviceMap(selectedDeviceMap)
        }
    }
}