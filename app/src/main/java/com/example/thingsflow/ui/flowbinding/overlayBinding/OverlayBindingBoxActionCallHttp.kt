package com.example.thingsflow.ui.flowbinding.overlayBinding

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionCallHttpBinding
import com.example.thingsflow.module.define.TFBodyHttpFormat
import com.example.thingsflow.module.define.TFHttpHeader
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterInOutput
import com.example.thingsflow.ui.adapter.AdapterJsonField
import com.example.thingsflow.ui.adapter.AdapterSpinnerBodyHttpFormat
import com.example.thingsflow.ui.adapter.AdapterSpinnerMethodCallHttpType
import com.example.thingsflow.ui.adapter.AdapterTableJsonField
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.define.FJsonField
import rogo.iot.module.flowcommon.type.FBoxType
import rogo.iot.module.flowcommon.type.FHttpType
import rogo.iot.module.flowcommon.type.FInputValueType

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
class OverlayBindingBoxActionCallHttp(
    context: Context,
    container: ViewGroup,
    private val onConfigHeader: () -> Unit,
    private val onConfigJsonOutput: (fields: MutableList<FJsonField>) -> Unit,
    private val onBoxActionCallHttpCreated: (FBoxActionCallHttp) -> Unit,
    private val onClose: (isBackable: Boolean) -> Unit
): OverlayBase<LayoutOverlayConfigBoxActionCallHttpBinding>(
    context,
    container,
    LayoutOverlayConfigBoxActionCallHttpBinding::inflate
) {
    private val vmFlowBinding: VMFlowBinding? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowBinding::class.java]
        }
    }

    private var fBoxActionCallHttp: FBoxActionCallHttp?= null
    private var inputFromParentBoxList: List<TFInputBoxValue?> = listOf() // list of input from previous box
    // hashmap to store headers that user insert
    private var requiredHeaders: HashMap<String, String> = hashMapOf()
    private var jsonFields: MutableList<FJsonField> = mutableListOf()

    private var map = hashMapOf<String, Int>()
    private val adapterInputFromPreviousBox: AdapterInOutput by lazy {
        AdapterInOutput()
    }

    private val adapterJsonField: AdapterJsonField by lazy {
        AdapterJsonField(
            onMenuClick = { parentField, returnToChild ->

            },
            onNotifyParent = {

            }
        )
    }
    // adapter of method http spinner
    private val adapterSpinnerMethodCallHttpType: AdapterSpinnerMethodCallHttpType by lazy {
        AdapterSpinnerMethodCallHttpType(
            context,
            listOf<Int>(
                FHttpType.GET,
                FHttpType.POST,
                FHttpType.DELETE
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

    private val adapterTableJsonField: AdapterTableJsonField by lazy {
        AdapterTableJsonField()
    }

    override fun onViewCreated(binding: LayoutOverlayConfigBoxActionCallHttpBinding) {
        binding.apply {

        }
    }

    override fun initUI() {
        super.initUI()
        setUpTabs()
        binding.apply {
            btnBack.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }
        }

        setUpInputLayout()
        setUpConfigLayout()
        setUpOutputLayout()
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
            spinnerMethodType.adapter = adapterSpinnerMethodCallHttpType
            spinnerBodyFormat.adapter = adapterSpinnerBodyHttpFormat

            btnClose.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }

            btnConfigHeader.setOnClickListener {
                onConfigHeader.invoke()
            }

            btnCreateBox.setOnClickListener {
                fBoxActionCallHttp?.url = edtUrl.text.toString()
                fBoxActionCallHttp?.headers = requiredHeaders
                fBoxActionCallHttp?.timeoutMs = edtTimeout.text.toString().toInt()
                fBoxActionCallHttp?.jsonFields = jsonFields.toTypedArray()
                onBoxActionCallHttpCreated.invoke(fBoxActionCallHttp!!)
            }

            spinnerMethodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val method = parent?.getItemAtPosition(position) as Int
                    when (method) {
                        FHttpType.POST -> {
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

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            rvJson.adapter = adapterJsonField
            rvTable.adapter = adapterTableJsonField

            btnTable.setBackgroundDrawable(context.getDrawable(R.drawable.btn_emerald))
            btnTable.setTextColor(context.getColor(R.color.white))
            btnJson.setBackgroundDrawable(context.getDrawable(R.drawable.btn_gray))
            btnJson.setTextColor(context.getColor(R.color.text_input))

            btnConfigOutput.setOnClickListener {
                onConfigJsonOutput.invoke(jsonFields)
            }

            btnOutputClose.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }

            btnOutputConfigClose.setOnClickListener {
                onClose.invoke(btnBack.isShown)
            }

            cbForwardJson.setOnCheckedChangeListener { _, isChecked ->
                cbExportValue.isChecked = !isChecked
            }

            cbExportValue.setOnCheckedChangeListener { _, isChecked ->
                cbForwardJson.isChecked = !isChecked
                if (isChecked && jsonFields.isNotEmpty()) lnShowOutputConfigured.show() else lnShowOutputConfigured.gone()
            }

            btnTable.setOnClickListener {
                btnTable.setBackgroundDrawable(context.getDrawable(R.drawable.btn_emerald))
                btnTable.setTextColor(context.getColor(R.color.white))
                btnJson.setBackgroundDrawable(context.getDrawable(R.drawable.btn_gray))
                btnJson.setTextColor(context.getColor(R.color.text_input))
                lnOutputTable.show()
                lnOutputJson.gone()
            }

            btnJson.setOnClickListener {
                btnTable.setBackgroundDrawable(context.getDrawable(R.drawable.btn_gray))
                btnTable.setTextColor(context.getColor(R.color.text_input))
                btnJson.setBackgroundDrawable(context.getDrawable(R.drawable.btn_emerald))
                btnJson.setTextColor(context.getColor(R.color.white))
                lnOutputTable.gone()
                lnOutputJson.show()
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            if (vmFlowBinding?.getSelectedBox() != null && vmFlowBinding?.getSelectedBox() is FBoxActionCallHttp) {
                fBoxActionCallHttp = vmFlowBinding!!.getSelectedBox() as FBoxActionCallHttp
            }
            requiredHeaders = fBoxActionCallHttp?.headers?: hashMapOf()
            initialize(
                fBoxActionCallHttp?.url,
                fBoxActionCallHttp?.timeoutMs,
                fBoxActionCallHttp?.jsonFields?: arrayOf()
            )
            cbForwardJson.isChecked = false
            cbExportValue.isChecked = true
            btnBack.show()
        }
    }

    fun show(headers: ArrayList<TFHttpHeader>) {
        super.show()
        requiredHeaders = hashMapOf()
        headers.forEach { header ->
            requiredHeaders[header.key] = header.value
        }
    }

    fun show(fieldList: Array<FJsonField>) {
        super.show()
        submitJsonFields(fieldList)
    }

    private fun initialize(url: String?, timeout: Int?, jsonFields: Array<FJsonField>) {
        binding.apply {
            tabLayout.getTabAt(1)?.select()
            edtUrl.setText(url?: "")
            edtTimeout.setText(timeout?.toString()?: "30000")

            submitJsonFields(jsonFields)
            showInputFromPreviousBox()
        }
    }

    /**
     * update UI to show json fields(json type and table type)
     */
    private fun submitJsonFields(
        fields: Array<FJsonField>
    ) {
        this@OverlayBindingBoxActionCallHttp.jsonFields = fields.toMutableList()
        if (jsonFields.isNotEmpty()) binding.lnShowOutputConfigured.show() else binding.lnShowOutputConfigured.gone()
        adapterJsonField.submitList(jsonFields)
        map = hashMapOf()
        mapJsonTable(jsonFields)
        adapterTableJsonField.submitList(map.entries.toList())
        binding.apply {
            lnOutputTable.gone()
            lnOutputJson.show()
        }
    }

    private fun mapJsonTable(fields: List<FJsonField>) {
        fields.forEach { field ->
            when(field.type) {
                FInputValueType.OBJECT -> {
                    mapJsonTable(field.fields.toList())
                }
                else -> {
                    map[field.jsonPath] = field.type
                }
            }
        }
    }

    private fun showInputFromPreviousBox() {
        binding.apply {
            //get parent box info
            val parentBox = getPreviousBox()

            var previousBoxType: Int = FBoxType.EVT_FROM_DEVICE
            parentBox?.let {
                vmFlowBinding?.getInputsFromParentBox(parentBox)?.let {
                    inputFromParentBoxList = vmFlowBinding?.getInputsFromParentBox(parentBox)!!
                }
                when(parentBox) {
                    is FBoxEventDevice -> {
                        previousBoxType = FBoxType.EVT_FROM_DEVICE
                        // get list of input from previous box
                        val device = FlowSdk.deviceHandler().get(parentBox.devId)
                        if (device != null) {
                            lnInput.lnPreviousBoxDevice.show()
                            val location = FlowSdk.locationHandler().get(device.locationId)
                            lnInput.txtPinputLabel.text = device.label
                            lnInput.txtPinputLocation.text = location.label
                        } else {
                            lnInput.lnPreviousBoxDevice.gone()
                        }

                    }
                    is FBoxActionCallHttp -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        previousBoxType = FBoxType.ACT_CALL_HTTP
                    }
                    else -> {
                        lnInput.lnPreviousBoxDevice.gone()
                        previousBoxType = FBoxType.ACT_CONTROL_DEVICE
                    }
                }
            }
            adapterInputFromPreviousBox.submitList(
                vmFlowBinding?.groupInputs(previousBoxType, inputFromParentBoxList)
            )

            lnInput.lnInputFromPreviousBox.show()
        }
    }

    private fun getPreviousBox(): FBox? =
        vmFlowBinding?.boxes?.value?.find { it.id == fBoxActionCallHttp?.rootId }


    private fun setUpTabs() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(
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
            if (output) lnOutput.show() else lnOutput.gone()
        }
    }
}