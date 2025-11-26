package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionDeviceStateBinding
import com.example.thingsflow.module.viewmodel.VMDevice
import com.example.thingsflow.ui.OverlayBase
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
    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionConditionDeviceStateBinding) {
        binding.apply {
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
                                lnInput.visibility = View.VISIBLE
                                lnConfig.visibility = View.GONE
                                lnOutput.visibility = View.GONE
                            }
                            else if (tab.position == 1) {
                                lnInput.visibility = View.GONE
                                lnConfig.visibility = View.VISIBLE
                                lnOutput.visibility = View.GONE
                            }
                            else {
                                lnInput.visibility = View.GONE
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

    fun show(devType: Int?, attrs: IntArray?, selectedDevices: HashMap<String?, IntArray>) {
        super.show()
        binding.apply {

        }
    }
}