package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionCallHttpBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionControlDeviceBinding
import com.example.thingsflow.module.model.ItemHeader
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerControlAction
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.adapter.AdapterSpinnerMethodCallHttpType
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedDeviceType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.platform.define.IoTAttribute

/**
 * @file: This overlay is used to configure a box action control device(FBoxActionControlDevice)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxActionControlDeviceCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxActionControlDevice(
    context: Context,
    container: ViewGroup,
    private val onSelectDevice: (Int?, IntArray?) -> Unit,
    private val onBoxActionControlDeviceCreated: (FBoxActionControlDevice) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionControlDeviceBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionControlDeviceBinding::inflate
) {
    //selectedDeviceMap is to store selected devices
    private var selectedDeviceMap: HashMap<String?, IntArray> = hashMapOf()

    //adapter for select devices
    private val adapterSelectedDevices: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
    }

    //adapter for select type of device
    private val adapterSpinnerDeviceType: AdapterSpinnerDeviceType by lazy {
        AdapterSpinnerDeviceType(context, getSupportedDeviceType())
    }

    //adapter for select type of action(onoff, lock-unlock,etc...)
    private val adapterSpinnerControlAction: AdapterSpinnerControlAction by lazy {
        AdapterSpinnerControlAction(
            context,
            listOf<Int>(
                IoTAttribute.ACT_ONOFF,
                IoTAttribute.ACT_OPEN_CLOSE,
                IoTAttribute.ACT_LOCK_UNLOCK
            )
        )
    }


    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionControlDeviceBinding) {
        binding.apply {
            cbLater.isChecked = true
            btnSelectDevice.isEnabled = false
            lnEmptyDevices.visibility = View.VISIBLE
            lnDevices.visibility = View.GONE
            selectedDeviceMap.clear()
            rvDevices.adapter = adapterSelectedDevices
            adapterSelectedDevices.submitList(selectedDeviceMap.entries.toList())
            spinnerDeviceType.adapter = adapterSpinnerDeviceType
            spinnerControlAction.adapter = adapterSpinnerControlAction
            tabLayoutEvtDevice.getTabAt(0)?.select()
            btnBack.setOnClickListener {
                onClose.invoke()
            }

            tabLayoutEvtDevice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) {

                            }
                            else if (tab.position == 1) {
                                lnConfig.visibility = View.VISIBLE
                                lnOutput.visibility = View.GONE
                            }
                            else {
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

            btnBack.setOnClickListener {

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

            btnClose.setOnClickListener {

            }

            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    intArrayOf()
                )
            }

            btnOutputConfig.setOnClickListener {
                tabLayoutEvtDevice.getTabAt(0)?.select()
            }

            btnOutputClose.setOnClickListener {

            }

            btnCreateBox.setOnClickListener {
                val fBox = FBoxActionControlDevice().apply {

                }
                onBoxActionControlDeviceCreated.invoke(fBox)
            }


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
                        onSelectDevice.invoke(
                            spinnerDeviceType.selectedItem as Int,
                            intArrayOf()
                        )
                    }
                } else {
                    btnSelectDevice.isEnabled = false
                }
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {

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