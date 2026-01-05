package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionGeneralBinding
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral

/**
 * @file: This overlay is used to configure a box action condition general(FBoxActionConditionGeneral)
 * It allows user to configure:
 * - compare value from the previous box with a inserted value
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param OverlayConfigBoxActionConditionGeneral: triggered when a box is setted up successfully
 * @param onClose: triggered when hide the overlay
 */
class OverlayConfigBoxActionConditionGeneral(
    context: Context,
    container: ViewGroup,
    private val onBoxActionCondtionGeneralCreated: (FBoxActionConditionGeneral) -> Unit,
    private val onClose: (Boolean) -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionConditionGeneralBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionConditionGeneralBinding::inflate
) {
    private val vmFlowScenario: VMFlowScenario? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScenario::class.java]
        }
    }

    private val adapterInOutput: AdapterInOutput by lazy {
        AdapterInOutput()
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionConditionGeneralBinding) {
        binding.apply {

        }
    }

    override fun initVariable() {
        super.initVariable()

    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            rvInputFromParentBox.adapter = adapterInOutput

            btnBack.setOnClickListener {
                onClose.invoke(true)
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke(false)
            }

            btnClose.setOnClickListener {
                onClose.invoke(false)
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

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
            )
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {

            btnCreateBox.setOnClickListener {
                val fBox = FBoxActionConditionGeneral().apply {
                    segId = "1"
                }
                onBoxActionCondtionGeneralCreated.invoke(fBox)
            }

        }
    }

    override fun show() {
        super.show()
        binding.apply {
            tabLayoutEvtDevice.getTabAt(0)?.select()
        }
        showInputFromPreviousBox()
    }

    private fun showInputFromPreviousBox() {
        binding.apply {
            val parentBoxId = vmFlowScenario?.getRootBoxId()
            val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
            parentBox?.let {
//                adapterInOutput.submitList(vmFlowScenario?.getInputsFromParentBox(parentBox))
            }
        }
    }

}