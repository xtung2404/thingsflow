package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.example.thingflowsdk.core.define.TFMethodHttp
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionCallHttpBinding
import com.example.thingsflow.module.model.ItemHeader
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterSpinnerMethodCallHttpType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp

/**
 * @file: This overlay is used to configure a box action call http(FBoxActionCallHttp)
 * It allows user to configure:
 * - Type of method call HTTP: GET, POST, etc...
 * - API url
 * - HTTP headers
 * - timeout
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onConfigHeader: triggered when user want to set up headers
 * @param onBoxActionCallHttpCreated: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxActionCallHttp(
    context: Context,
    container: ViewGroup,
    private val onConfigHeader: () -> Unit,
    private val onBoxActionCallHttpCreated: (FBoxActionCallHttp) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionCallHttpBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionCallHttpBinding::inflate
) {
    // hashmap to store headers that user insert
    private val requiredHeaders: HashMap<String, String> = hashMapOf()

    // adapter of method http spinner
    private val adapterSpinnerMethodCallHttpType: AdapterSpinnerMethodCallHttpType by lazy {
        AdapterSpinnerMethodCallHttpType(
            context,
            listOf<TFMethodHttp>(
                TFMethodHttp.GET,
                TFMethodHttp.POST,
                TFMethodHttp.DELETE
            )
        )
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionCallHttpBinding) {
        binding.apply {
            spinnerMethodType.adapter = adapterSpinnerMethodCallHttpType
            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnConfigHeader.setOnClickListener {
                onConfigHeader.invoke()
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
                    url = edtUrl.text.toString()
                    headers = requiredHeaders
                    timeoutMs = edtTimeout.text.toString().toInt()
                }
                onBoxActionCallHttpCreated.invoke(fBox)
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke()
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            edtUrl.setText("")
        }
    }

    fun show(headers: ArrayList<ItemHeader>) {
        super.show()
        requiredHeaders.clear()
        headers.forEach { header ->
            requiredHeaders[header.key] = header.value
        }
    }
}