package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionCallHttpBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionGeneralBinding
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxEventFromDeviceBinding
import com.example.thingsflow.module.model.TypeCallHttpMethod
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterAttributes
import com.example.thingsflow.ui.adapter.AdapterSpinnerDeviceType
import com.example.thingsflow.ui.adapter.AdapterSpinnerMethodCallHttpType
import com.example.thingsflow.ui.dialog.DialogDeviceList
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getSupportedAttribue
import com.example.thingsflow.utils.getSupportedDeviceType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.rogocore.sdk.SmartSdk
import kotlin.collections.get
import kotlin.text.set

class OverlayConfigBoxActionCallHttp(
    context: Context,
    container: ViewGroup,
    private val onBoxActionCallHttpCreated: (FBoxActionCallHttp) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionCallHttpBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionCallHttpBinding::inflate
) {
    private val adapterSpinnerMethodCallHttpType: AdapterSpinnerMethodCallHttpType by lazy {
        AdapterSpinnerMethodCallHttpType(
            context,
            listOf<TypeCallHttpMethod>(
                TypeCallHttpMethod.GET,
                TypeCallHttpMethod.POST,
                TypeCallHttpMethod.DELETE
            )
        )
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionCallHttpBinding) {
        binding.apply {
            spinnerMethodType.adapter = adapterSpinnerMethodCallHttpType
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

            btnCreateBox.setOnClickListener {
                val fBox = FBoxActionCallHttp().apply {

                }
                onBoxActionCallHttpCreated.invoke(fBox)
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke()
            }
        }
    }
}