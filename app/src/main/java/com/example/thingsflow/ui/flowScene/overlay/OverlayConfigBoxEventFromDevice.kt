package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
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

class OverlayConfigBoxEventFromDevice(
    context: Context,
    container: ViewGroup,
    private val onBoxEventCreated: (FBoxEventDevice) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayConfigBoxEventFromDeviceBinding>(
    context,
    container,
    LayoutOverlayConfigBoxEventFromDeviceBinding::inflate
) {
    private var attrMap: MutableMap<Pair<Int, String>, Boolean>  = mutableMapOf()

    private val dialogDeviceList: DialogDeviceList by lazy {
        DialogDeviceList(
            context,
            onDeviceSelected = {
                binding.apply {
                    txtDeviceLabel.text = SmartSdk.deviceHandler().get(it.first)?.label
                }
            }
        )
    }

    private val adapterAttributes: AdapterAttributes by lazy {
        AdapterAttributes(
            onItemClicked = {
                attrMap[it] = !attrMap[it]!!
                adapterAttributes.notifyDataSetChanged()
            }
        )
    }

    override fun show() {
        super.show()
        binding.tabLayoutEvtDevice.getTabAt(0)?.select()
    }

    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(context, getSupportedDeviceType())
    }
    override fun onViewCreated(binding: LayoutOverlayConfigBoxEventFromDeviceBinding) {
        binding.apply {
            cbLater.isChecked = true
            btnSelectDevice.isEnabled = false
            rvAttr.adapter = adapterAttributes
            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            tabLayoutEvtDevice.getTabAt(0)?.select()
            // map every attribute to its label and set it to false(or unselected)
            attrMap = getSupportedAttribue()
                .map { (it to getAttrLabel(context, it)) to false }
                .toMap()
                .toMutableMap()
            adapterAttributes.submitList(attrMap.entries.toList())

            btnBack.setOnClickListener {
                onClose.invoke()
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
                onClose.invoke()
            }

            btnSelectDevice.setOnClickListener {
                dialogDeviceList.show()
            }

            btnOutputConfig.setOnClickListener {
                tabLayoutEvtDevice.getTabAt(0)?.select()
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke()
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
                } else {
                    btnSelectDevice.isEnabled = false
                }
            }
        }
    }
}