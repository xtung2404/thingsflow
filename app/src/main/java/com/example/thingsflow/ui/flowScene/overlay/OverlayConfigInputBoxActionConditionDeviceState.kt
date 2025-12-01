package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionDeviceStateBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigInputBoxActionConditionDeviceStateBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedDeviceType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState
import rogo.iot.module.rogocore.sdk.SmartSdk
import kotlin.math.ln

/**
 * @file: This overlay is used to configure input for a box action condition device state(FBoxActionConditionDeviceState)
 * It allows user to configure:
 * - select device type, attributes or a specific device
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onSelectDevice: triggered when user want to select a specific device
 * @param onInputSetted: triggered when user configed input successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigInputBoxActionConditionDeviceState(
    context: Context,
    container: ViewGroup,
    private val onSelectDevice: (devType: Int?, attrs: IntArray?) -> Unit,
    private val onInputSetted: (devType: Int, attrs: IntArray, selectedDevices: HashMap<String?, IntArray>) -> Unit,
    private val onClose: (Boolean) -> Unit
) : OverlayBase<LayoutOverlayConfigInputBoxActionConditionDeviceStateBinding>(
    context,
    container,
    LayoutOverlayConfigInputBoxActionConditionDeviceStateBinding::inflate
) {

    // attrMap to store state of attributes: selected or unselected
    private var attrMap: MutableMap<Pair<Int, String>, Boolean> = mutableMapOf()

    // selectedDeviceMap to store selected devices
    // key: uuid of device, value: selected elements of device
    private var selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    // adapter for select device type
    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(context, getSupportedDeviceType())
    }

    // adapter for select attribute
    private val adapterAttributes: AdapterAttributes by lazy {
        AdapterAttributes(
            onItemClicked = {
                // set state of attribute to selected or unselected
                attrMap[it] = !attrMap[it]!!
                adapterAttributes.notifyDataSetChanged()
            }
        )
    }

    private val adapterSelectedDevices: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
    }

    override fun onViewCreated(binding: LayoutOverlayConfigInputBoxActionConditionDeviceStateBinding) {
        binding.apply {
            // set to select device later
            selectedDeviceMap.clear()
            cbLater.isChecked = true
            btnSelectDevice.isEnabled = false
            lnEmptyDevices.visibility = View.VISIBLE
            lnDevices.visibility = View.GONE

            //bind adapter to recyclerview and spinner
            rvAttr.adapter = adapterAttributes
            spinnerDeviceType.adapter = adapterSpinnerDeviceType

            // map every attribute to its label and set it to false(or unselected)
            attrMap = getSupportedAttribue()
                .map { (it to getAttrLabel(context, it)) to false }
                .toMap()
                .toMutableMap()
            adapterAttributes.submitList(attrMap.entries.toList())

            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            btnSelectDevice.setOnClickListener {
                val selectedAttrs = intArrayOf()
                attrMap.filter {
                    it.value
                }.keys.forEach {
                    selectedAttrs.plus(it.first)
                }
                onSelectDevice.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    selectedAttrs
                )
            }

            btnConfig.setOnClickListener {
                val selectedAttrs = intArrayOf()
                attrMap.filter {
                    it.value
                }.keys.forEach {
                    selectedAttrs.plus(it.first)
                }
                onInputSetted.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    selectedAttrs,
                    selectedDeviceMap
                )
            }

            edtAttr.addTextChangedListener(
                object : TextWatcher {
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
                        s?.let { searchInput ->
                            //filter attributes according to user
                            val searchedList = attrMap.entries
                                .filter { it.key.second.contains(searchInput, true) }
                                .sortedBy { it.key.second }
                                .toMutableList()

                            adapterAttributes.submitList(searchedList)
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                }
            )

            cbLater.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbSelectDevice.isChecked = false
                    btnSelectDevice.isEnabled = false
                }
            }

            cbSelectDevice.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbLater.isChecked = false
                    btnSelectDevice.isEnabled = true
                    if (selectedDeviceMap.isEmpty) {
                        val selectedAttrs = intArrayOf()
                        attrMap.filter {
                            it.value
                        }.keys.forEach {
                            selectedAttrs.plus(it.first)
                        }
                        onSelectDevice.invoke(
                            spinnerDeviceType.selectedItem as Int,
                            selectedAttrs
                        )
                    }
                } else {
                    btnSelectDevice.isEnabled = false
                }
            }
        }
    }

    fun show(devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        super.show()
        binding.apply {
            selectedDeviceMap = selectedDevices
            devType?.let {
                val devTypePos = adapterSpinnerDeviceType.getPosition(it)
                if (devTypePos != -1) {
                    spinnerDeviceType.setSelection(devTypePos)
                }
            }
            if (selectedDevices.isNotEmpty()) {
                lnEmptyDevices.visibility = View.GONE
                lnDevices.visibility = View.VISIBLE
                rvDevices.adapter = adapterSelectedDevices
                adapterSelectedDevices.submitList(selectedDevices.entries.toList())
            }
        }
    }
}