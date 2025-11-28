package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionControlDeviceBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerControlAction
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.utils.getControlableDeviceType
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice
//import rogo.iot.module.base.define.IoTAttribute

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
    private lateinit var adapterSpinnerDeviceType: AdapterSpinnerDeviceType

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
                                lnInput.show()
                                lnConfig.gone()
                                lnOutput.gone()
                            }
                            else if (tab.position == 1) {
                                lnInput.gone()
                                lnConfig.show()
                                lnOutput.gone()
                            } else {
                                lnInput.gone()
                                lnConfig.gone()
                                lnOutput.show()
                            }

                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                }
            )

            spinnerControlAction.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val action = spinnerControlAction.selectedItem as Int
                    adapterSpinnerDeviceType = AdapterSpinnerDeviceType(
                        context, getControlableDeviceType(action)
                    )
                    spinnerDeviceType.adapter = adapterSpinnerDeviceType
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

            btnClose.setOnClickListener {
                onClose.invoke()
            }

            btnSelectDevice.setOnClickListener {
                onSelectDevice.invoke(
                    spinnerDeviceType.selectedItem as Int,
                    intArrayOf(
                        spinnerControlAction.selectedItem as Int
                    )
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
                            intArrayOf(
                                spinnerControlAction.selectedItem as Int
                            )
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

    fun show(box: FBox?) {
        binding.apply {
            if (box is FBoxActionControlDevice) {
//                inititialize(box.devType, box.elms)
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

    private fun inititialize(devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        binding.apply {

        }
    }
}