package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.base.define.TFComparision
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionConditionGeneralBinding
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.viewmodel.VMFlowScene
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.ui.adapter.AdapterSpinnerComparision
import com.example.thingsflow.ui.adapter.AdapterSpinnerInput
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.type.FBoxType
import rogo.iot.module.flowcommon.type.FInputValueType
import rogo.iot.module.flowcommon.value.FInputValue

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
) : OverlayBase<LayoutOverlayConfigBoxActionConditionGeneralBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionConditionGeneralBinding::inflate
) {
    private val vmFlowScene: VMFlowScene? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScene::class.java]
        }
    }
    private var fBoxActionConditionGeneral: FBoxActionConditionGeneral? = null
    private var inputFromParentBoxList: List<TFInputBoxValue?> =
        listOf() // list of input from previous box
    private var selectedComparedValue: TFInputBoxValue? = null
    private var selectedComparisionType: Int? = null

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
                createBoxActionConditionGeneral()
            }

            spinnerComparedValue.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        lnConfigComparedValue.gone()
                        if (spinnerComparedValue.selectedItem != null) {
                            val comparedValue = spinnerComparedValue.selectedItem as TFInputBoxValue
                            selectedComparedValue = comparedValue
                            adapterSpinnerComparision = AdapterSpinnerComparision(
                                context,
                                TFComparision.getComparionTypes(context, comparedValue.input.type)
                                    .toList()
                            )
                            spinnerComparisionType.adapter = adapterSpinnerComparision
                            if (fBoxActionConditionGeneral != null && selectedComparisionType != null) {
                                val comparisionTypePos = adapterSpinnerComparision.getPosition(selectedComparisionType)
                                if (comparisionTypePos != -1) {
                                    spinnerComparisionType.setSelection(comparisionTypePos)
                                }
                            }
                            lnConfigComparedValue.show()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedComparedValue = null
                        lnConfigComparedValue.gone()
                    }
                }

            spinnerComparisionType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val comparisionType = parent?.getItemAtPosition(position) as Int
                        selectedComparisionType = comparisionType
                        if (selectedComparisionType == TFComparision.BETWEEN) {
                            lnComparingInRange.show()
                            lnComparingValue.gone()
                        } else {
                            lnComparingInRange.gone()
                            lnComparingValue.show()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedComparisionType = null
                        lnComparingInRange.gone()
                        lnComparingValue.gone()
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
            btnBack.show()
            edtComparingValue.setText("")
            fBoxActionConditionGeneral = null
            tabLayoutEvtDevice.getTabAt(0)?.select()
            showInputFromPreviousBox()
        }
    }

    /**
     * show information of a box
     * @param fBox: box
     */
    fun show(fBox: FBoxActionConditionGeneral?) {
        super.show()
        binding.apply {
            btnBack.gone()
            edtComparingValue.setText("")
            fBoxActionConditionGeneral = fBox
            fBoxActionConditionGeneral?.let {
                showInputFromPreviousBox()
                selectedComparisionType = fBoxActionConditionGeneral?.condition
                if (fBoxActionConditionGeneral!!.comparedValue != null) {
                    val comparedValuePos =
                        inputFromParentBoxList.indexOfFirst {
                            it?.inputType == TFInOutType.JSON_FIELD && it.input.type == fBoxActionConditionGeneral!!.comparedValue!![0].type && it.input.value == fBoxActionConditionGeneral!!.comparedValue!![0].value

                        }
                    if (comparedValuePos != -1) {
                        spinnerComparedValue.setSelection(comparedValuePos)
                        val comparingValue = fBoxActionConditionGeneral!!.comparingValue[0]
                        comparingValue?.let {
                            edtComparingValue.setText(comparingValue.value.toString())
                        }
                    }
                }
            }
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
            parentBox?.let {
                vmFlowScene?.getInputsFromParentBox(parentBox)?.let {
                    inputFromParentBoxList = vmFlowScene?.getInputsFromParentBox(parentBox)!!
                }
                when (parentBox) {
                    is FBoxEventDevice -> {
                        lnInput.lnPreviousBoxDevice.show()
                    }

                    is FBoxActionCallHttp -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        adapterInputFromPreviousBox.submitList(
                            vmFlowScene?.groupInputs(
                                FBoxType.ACT_CALL_HTTP,
                                inputFromParentBoxList
                            )
                        )
                        lnInput.lnInputFromPreviousBox.show()
                        if (inputFromParentBoxList.isEmpty()) {
                            inputFromParentBoxList = listOf(null)
                        }
                        adapterComparedValue = AdapterSpinnerInput(
                            context,
                            inputFromParentBoxList
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
        val previousBoxId =
            if (fBoxActionConditionGeneral == null) vmFlowScene?.getRootBoxId() else fBoxActionConditionGeneral?.rootId
        return vmFlowScene?.boxes?.value?.find { it.id == previousBoxId }
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

    /**
     * create a new box
     */
    private fun createBoxActionConditionGeneral() {
        if (fBoxActionConditionGeneral == null) {
            fBoxActionConditionGeneral = FBoxActionConditionGeneral()
        }
        //get comparing value
        val rawValue = binding.edtComparingValue.text.toString()
        //convert the comparing value to the same type as compared value
        val convertedValue: Any = try {
            when (selectedComparedValue!!.input.type) {
                FInputValueType.INTEGER -> rawValue.toInt()
                FInputValueType.FLOAT -> rawValue.toDouble()
                FInputValueType.BOOLEAN -> rawValue.toBooleanStrict()
                FInputValueType.STRING -> rawValue
                else -> rawValue
            }
        } catch (e: Exception) {
            return
        }
        selectedComparedValue?.let {
            fBoxActionConditionGeneral?.comparedValue = arrayOf(
                FInputValue(
                    selectedComparedValue!!.input.type,
                    selectedComparedValue!!.input.value
                )
            )
        }
        selectedComparisionType?.let { fBoxActionConditionGeneral?.condition = selectedComparisionType!! }
        if (!rawValue.isBlank()) {
            fBoxActionConditionGeneral?.comparingValue = arrayOf(
                FInputValue(
                    selectedComparedValue!!.input.type,
                    convertedValue
                )
            )
        }
        onBoxActionCondtionGeneralCreated.invoke(fBoxActionConditionGeneral!!)
    }
}