package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedDeviceType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.rogocore.sdk.SmartSdk
import kotlin.collections.get
import kotlin.text.set

/**
 * @file: This overlay is used to configure a box event from device(FBoxActionCallHttp)
 * It allows user to configure:
 * - set a device as a trigger for the flow
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxEventCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxEventFromDevice(
    context: Context,
    container: ViewGroup,
    private val onBoxEventCreated: (FBoxEventDevice) -> Unit,
    private val onClose: (Boolean) -> Unit
): OverlayBase<LayoutOverlayConfigBoxEventFromDeviceBinding>(
    context,
    container,
    LayoutOverlayConfigBoxEventFromDeviceBinding::inflate
) {
    // hashmap to check whether attributes is selected or not
    //key: information of attribure. first: attribure, second: label of attribute
    //value: is attribute selected
    private var attrMap: MutableMap<Pair<Int, String>, Boolean>  = mutableMapOf()

    // hashmap to store selected devices
    // key: uuid of device, value: selected elements of device
    private val selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    //adapter for selected devices
    private val adapterSelectedDevices: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
    }

    // adapter for select device type
    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(context, getSupportedDeviceType())
    }

    // adapter for selecting attribute
    private val adapterAttributes: AdapterAttributes by lazy {
        AdapterAttributes(
            onItemClicked = {
                attrMap[it] = !attrMap[it]!!
                adapterAttributes.notifyDataSetChanged()
            }
        )
    }

    // this dialog shows up when user want to select a device
    private val dialogDeviceList: DialogDeviceList by lazy {
        DialogDeviceList(
            context,
            onDeviceSelected = { uuid, elms ->
                binding.apply {
                    selectedDeviceMap.clear()
                    lnEmptyDevices.visibility = View.GONE
                    lnDevices.visibility = View.VISIBLE
                    // set selected device to hashmap
                    selectedDeviceMap[uuid] = elms
                    adapterSelectedDevices.submitList(selectedDeviceMap.entries.toList())
                }
            }
        )
    }

    override fun show() {
        super.show()
        binding.tabLayoutEvtDevice.getTabAt(0)?.select()
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxEventFromDeviceBinding) {
        binding.apply {
            // set option to select device later
            selectedDeviceMap.clear()
            cbLater.isChecked = true
            btnSelectDevice.isEnabled = false
            lnEmptyDevices.visibility = View.VISIBLE
            lnDevices.visibility = View.GONE

            // set adapter for recyclerview and spinner
            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            rvAttr.adapter = adapterAttributes
            rvDevices.adapter = adapterSelectedDevices
            adapterSelectedDevices.submitList(selectedDeviceMap.entries.toList())

            tabLayoutEvtDevice.getTabAt(0)?.select()
            // map every attribute to its label and set it to false(or unselected)
            attrMap = getSupportedAttribue()
                .map { (it to getAttrLabel(context, it)) to false }
                .toMap()
                .toMutableMap()
            adapterAttributes.submitList(attrMap.entries.toList())

            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            tabLayoutEvtDevice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) {
                                lnConfig.visibility = View.VISIBLE
                                lnOutput.visibility = View.GONE
                            }
                            else if (tab.position == 1) {
                                lnConfig.visibility = View.GONE
                                lnOutput.visibility = View.VISIBLE
                            }

                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                }
            )

            btnCreateBox.setOnClickListener {
                // check selected attributes
                val selectedAttrs = arrayListOf<Int>()
                attrMap.filter {
                    it.value
                }.keys.forEach {
                    selectedAttrs.add(it.first)
                }
                val fBox = FBoxEventDevice().apply {
                    devId = ""
                    devType = spinnerDeviceType.selectedItem as Int
                    attrTypes = selectedAttrs.toIntArray()
                }
                onBoxEventCreated.invoke(fBox)
            }
            btnClose.setOnClickListener {
                onClose.invoke(false)
            }

            btnSelectDevice.setOnClickListener {
                dialogDeviceList.show()
            }

            btnOutputConfig.setOnClickListener {
                tabLayoutEvtDevice.getTabAt(0)?.select()
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke(true)
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
                        s?.let {searchInput ->
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
                    if (selectedDeviceMap.isEmpty()) {
                        dialogDeviceList.show()
                    }
                } else {
                    btnSelectDevice.isEnabled = false
                }
            }
        }
    }

    fun show(isBackable: Boolean) {
        binding.apply {
            if (isBackable) {
                btnBack.visibility = View.VISIBLE
            } else {
                btnBack.visibility = View.GONE
            }
        }
    }
}