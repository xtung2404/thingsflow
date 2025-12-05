package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.define.TFMethodHttp
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionCallHttpBinding
import com.example.thingsflow.module.define.TFBodyHttpFormat
import com.example.thingsflow.module.define.TFItemHeader
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterInput
import com.example.thingsflow.ui.adapter.AdapterSpinnerBodyHttpFormat
import com.example.thingsflow.ui.adapter.AdapterSpinnerMethodCallHttpType
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
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

    private val vmFlowScenario: VMFlowScenario? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScenario::class.java]
        }
    }

    private val adapterInput: AdapterInput by lazy {
        AdapterInput()
    }
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

    private val adapterSpinnerBodyHttpFormat: AdapterSpinnerBodyHttpFormat by lazy {
        AdapterSpinnerBodyHttpFormat(
            context,
            listOf<TFBodyHttpFormat>(
                TFBodyHttpFormat.JSON_FORMAT
            )
        )
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionCallHttpBinding) {
        binding.apply {

        }
    }

    override fun initVariable() {
        super.initVariable()

    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            spinnerMethodType.adapter = adapterSpinnerMethodCallHttpType
            spinnerBodyFormat.adapter = adapterSpinnerBodyHttpFormat
            rvInputFromParentBox.adapter = adapterInput


            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnConfigHeader.setOnClickListener {
                onConfigHeader.invoke()
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke()
            }

            tabLayout.addOnTabSelectedListener(
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

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
            )

            spinnerMethodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val method = parent?.getItemAtPosition(position) as TFMethodHttp
                    when (method) {
                        TFMethodHttp.POST -> {
                            lnConfigBody.show()
                        }
                        else -> {
                            lnConfigBody.gone()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerBodyFormat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val bodyFormat = parent?.getItemAtPosition(position) as TFBodyHttpFormat
                    when (bodyFormat) {
                        TFBodyHttpFormat.JSON_FORMAT -> {

                        }
                        else -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnCreateBox.setOnClickListener {
                val fBox = FBoxActionCallHttp().apply {
                    url = edtUrl.text.toString()
                    headers = requiredHeaders
                    timeoutMs = edtTimeout.text.toString().toInt()
                }
                onBoxActionCallHttpCreated.invoke(fBox)
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            tabLayout.getTabAt(0)?.select()
            edtUrl.setText("")
        }
        showInputFromPreviousBox()
    }

    fun show(headers: ArrayList<TFItemHeader>) {
        super.show()
        requiredHeaders.clear()
        headers.forEach { header ->
            requiredHeaders[header.key] = header.value
        }
        showInputFromPreviousBox()

        binding.apply {
            tabLayout.getTabAt(1)?.select()
        }

    }

    private fun showInputFromPreviousBox() {
        binding.apply {
            val parentBoxId = vmFlowScenario?.getRootBoxId()
            val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
            parentBox?.let {
                adapterInput.submitList(vmFlowScenario?.getInputsFromParentBox(parentBox))
            }
        }
    }
}