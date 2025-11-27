package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.define.TFComparision
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionDeviceStateBinding
import com.example.thingsflow.module.define.TFInputSource
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterSelectedDevice
import com.example.thingsflow.ui.adapter.AdapterSpinnerComparision
import com.example.thingsflow.ui.adapter.AdapterSpinnerInputSource
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState

/**
 * @file: This overlay is used to configure a box action condition device state(FBoxActionConditionDeviceState)
 * It allows user to configure:
 * - compare state of a device or a value from the previous box with a specific state
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onConfigInput: triggered when user want to create input from other devices
 * @param onBoxActionCondtionDeviceStateCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxActionConditionDeviceState(
    context: Context,
    container: ViewGroup,
    private val onConfigInput: () -> Unit,
    private val onBoxActionCondtionDeviceStateCreated: (FBoxActionConditionDeviceState) -> Unit,
    private val onClose: (Boolean) -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionConditionDeviceStateBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionConditionDeviceStateBinding::inflate
) {
    private val vmDevice: VMDevice? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMDevice::class.java]
        }
    }

    private val adapterSelectedDevice: AdapterSelectedDevice by lazy {
        AdapterSelectedDevice()
    }

    private val adapterSpinnerInputSource: AdapterSpinnerInputSource by lazy {
        AdapterSpinnerInputSource(
            context,
            listOf<Int>(
                TFInputSource.INPUT_FROM_PREVIOUS_BOX,
                TFInputSource.INPUT_FROM_OTHER_DEVICES
            ))
    }

    private val adapterSpinnerComparision: AdapterSpinnerComparision by lazy {
        AdapterSpinnerComparision(
            context,
            listOf<Int>(
                TFComparision.EQUAL,
                TFComparision.DIFF
            )
        )
    }
    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionConditionDeviceStateBinding) {
        binding.apply {
            txtDeviceType.text = ""
            txtAttr.text = ""

            rvSelectedDevices.adapter = adapterSelectedDevice
            spinnerInputType.adapter = adapterSpinnerInputSource
            spinnerComparisionType.adapter = adapterSpinnerComparision

            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            btnConfigInput.setOnClickListener {
                onConfigInput.invoke()
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
                            }
                            else {
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

            btnCreateBox.setOnClickListener {
                val fBox = FBoxActionConditionDeviceState().apply {
                    segId = "1"
                }
                onBoxActionCondtionDeviceStateCreated.invoke(fBox)
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke(false)
            }

            btnClose.setOnClickListener {
                onClose.invoke(false)
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            btnConfigInput.show()
            lnInputConfigured.gone()
        }
    }

    fun show(devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        super.show()
        binding.apply {
            btnConfigInput.gone()
            lnInputConfigured.show()
            devType?.let {
                txtDeviceType.text = getDeviceTypeLabel(context, it)
            }
            adapterSelectedDevice.submitList(selectedDevices.entries.toList())
        }
    }
}