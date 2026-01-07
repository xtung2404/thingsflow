package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingflowsdk.core.base.define.TFComparision
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionGeneralBinding
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.define.TFInputSource
import com.example.thingsflow.module.viewmodel.VMFlowScenario
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.ui.adapter.AdapterSpinnerComparision
import com.example.thingsflow.ui.adapter.AdapterSpinnerInput
import com.example.thingsflow.ui.adapter.AdapterSpinnerInputSource
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.type.FBoxType
import rogo.iot.module.flowcommon.type.FInputValueType

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
    private var fBoxActionConditionGeneral: FBoxActionConditionGeneral?= null

    private var inputFromParentBoxList: List<TFInputBoxValue>? = listOf() // list of input from previous box
    private val adapterInputFromPreviousBox: AdapterInOutput by lazy {
        AdapterInOutput()
    }
    private lateinit var adapterComparedValue: AdapterSpinnerInput
    private lateinit var adapterSpinnerComparision: AdapterSpinnerComparision
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
            setUpTabs()

            setUpInputLayout()
            setUpConfigLayout()
            setUpOutputLayout()
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {

        }
    }

    private fun setUpInputLayout() {
        binding.apply {
            lnInput.lnInputFromPreviousBox.show()
            lnInput.lnInputFromSpecificDevice.gone()
            lnInput.rvInputFromParentBox.adapter = adapterInputFromPreviousBox
        }
    }

    private fun setUpConfigLayout() {
        binding.apply {
            btnCreateBox.setOnClickListener {
                val fBox = FBoxActionConditionGeneral().apply {

                }
                onBoxActionCondtionGeneralCreated.invoke(fBox)
            }

            spinnerComparedValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val comparedValue = spinnerComparedValue.selectedItem as TFInputBoxValue
                    adapterSpinnerComparision = AdapterSpinnerComparision(
                        context,
                        TFComparision.getComparionTypes(context, comparedValue.input.type).toList()
                    )
                    spinnerComparisionType.adapter = adapterSpinnerComparision
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

            btnBack.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }

            btnClose.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            btnOutputClose.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            tabLayoutEvtDevice.getTabAt(0)?.select()
            showInputFromPreviousBox()
        }
    }

    /**
     * show list of input from previous box
     * logic:
     * - get information of previous box
     * - check box type, only when previous box is box event device, the input from previous box can be shown
     */
    private fun showInputFromPreviousBox() {
        binding.apply {
            //get parent box info
            val parentBox = getPreviousBox()
            inputFromParentBoxList = vmFlowScenario?.getInputsFromParentBox(parentBox)
            parentBox?.let {
                when(parentBox) {
                    is FBoxEventDevice -> {
                        lnInput.lnPreviousBoxDevice.show()
                    }
                    is FBoxActionCallHttp -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        adapterInputFromPreviousBox.submitList(
                            vmFlowScenario?.groupInputs(FBoxType.ACT_CALL_HTTP, inputFromParentBoxList)
                        )
                        lnInput.lnInputFromPreviousBox.show()
                        adapterComparedValue = AdapterSpinnerInput(
                            context,
                            inputFromParentBoxList!!
                        )
                        spinnerComparedValue.adapter = adapterComparedValue
                    }
                    else -> {
                        lnInput.lnInputFromPreviousBox.gone()
                    }
                }
            }
        }
    }

    private fun getPreviousBox(): FBox? {
        val previousBoxId = if (fBoxActionConditionGeneral == null) vmFlowScenario?.getRootBoxId() else fBoxActionConditionGeneral?.rootId
        return vmFlowScenario?.boxes?.value?.find { it.id == previousBoxId }
    }

    private fun setUpTabs() {
        binding.apply {
            tabLayoutEvtDevice.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            if (tab.position == 0) showTab(input = true)
                            else if (tab.position == 1) showTab(config = true)
                            else showTab(output = true)
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                }
            )
        }
    }

    private fun showTab(input: Boolean = false, config: Boolean = false, output: Boolean = false) {
        binding.apply {
            if (input) lnInput.root.show() else lnInput.root.gone()
            if (config) lnConfig.show() else lnConfig.gone()
            if (output) lnOutput.show() else lnOutput.keepScreenOn
        }
    }

}