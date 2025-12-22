package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.thingflowsdk.core.base.define.TFMethodHttp
import com.example.thingsflow.databinding.LayoutOverlayConfigBoxActionCallHttpBinding
import com.example.thingsflow.module.define.TFBodyHttpFormat
import com.example.thingsflow.module.define.TFItemHeader
import com.example.thingsflow.module.define.TFJsonField
import com.example.thingsflow.module.define.TFPrimitiveType
import com.example.thingsflow.module.viewmodel.VMFlowScenario
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
    private val onConfigJsonOutput: () -> Unit,
    private val onBoxActionCallHttpCreated: (FBoxActionCallHttp) -> Unit,
    private val onClose: (isBackable: Boolean) -> Unit
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

    private var fBoxActionCallHttp: FBoxActionCallHttp?= null

    val map = hashMapOf<String, TFPrimitiveType>()
    private val adapterInOutput: AdapterInOutput by lazy {
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
    // hashmap to store headers that user insert
    private var requiredHeaders: HashMap<String, String> = hashMapOf()

    private val jsonFields: ArrayList<TFJsonField> = arrayListOf()

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
            rvInputFromParentBox.adapter = adapterInOutput
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
                if (fBoxActionCallHttp == null) {
                    fBoxActionCallHttp = FBoxActionCallHttp()
                }
                fBoxActionCallHttp?.url = edtUrl.text.toString()
                fBoxActionCallHttp?.headers = requiredHeaders
                fBoxActionCallHttp?.timeoutMs = edtTimeout.text.toString().toInt()
                onBoxActionCallHttpCreated.invoke(fBoxActionCallHttp!!)
            }

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

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setUpOutputLayout() {
        binding.apply {
            rvJson.adapter = adapterJsonField
            rvTable.adapter = adapterTableJsonField

            btnConfigOutput.setOnClickListener {
                onConfigJsonOutput.invoke()
            }

            btnOutputClose.setOnClickListener {
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
                lnOutputTable.show()
                lnOutputJson.gone()
            }

            btnJson.setOnClickListener {
                lnOutputTable.gone()
                lnOutputJson.show()
            }
        }
    }

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
            if (input) lnInput.show() else lnInput.gone()
            if (config) lnConfig.show() else lnConfig.gone()
            if (output) lnOutput.show() else lnOutput.gone()
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            fBoxActionCallHttp = null
            requiredHeaders = hashMapOf()
            tabLayout.getTabAt(0)?.select()
            edtUrl.setText("")
            if (edtTimeout.text.toString().isEmpty()) edtTimeout.setText("30000")
            cbForwardJson.isChecked = false
            cbExportValue.isChecked = true
            btnBack.show()
        }
        showInputFromPreviousBox()
    }

    fun show(fBox: FBox?) {
        super.show()
        binding.apply {
            btnBack.gone()
            if (fBox is FBoxActionCallHttp) {
                fBoxActionCallHttp = fBox
                requiredHeaders = fBoxActionCallHttp?.headers?: hashMapOf()
                edtUrl.setText(fBoxActionCallHttp?.url)
                edtTimeout.setText(fBoxActionCallHttp?.timeoutMs.toString())
            }
        }
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

    fun show(fieldList: List<TFJsonField>) {
        super.show()
        jsonFields.clear()
        jsonFields.addAll(fieldList)
        if (jsonFields.isNotEmpty()) binding.lnShowOutputConfigured.show() else binding.lnShowOutputConfigured.gone()
        adapterJsonField.submitList(jsonFields)
        mapJsonTable(jsonFields)

        adapterTableJsonField.submitList(map.entries.toList())
    }

    private fun mapJsonTable(fields: List<TFJsonField>) {
        fields.forEach { field ->
            when(field.type) {
                TFPrimitiveType.OBJECT -> {
                    mapJsonTable(field.fields)
                }
                else -> {
                    map[field.jsonPath] = field.type
                }
            }
        }
    }

    private fun showInputFromPreviousBox() {
        binding.apply {
            val parentBoxId = vmFlowScenario?.getRootBoxId()
            val parentBox = vmFlowScenario?.boxes?.value?.find { it.id == parentBoxId }
            parentBox?.let {
                adapterInOutput.submitList(vmFlowScenario?.getInputsFromParentBox(parentBox))
            }
        }
    }

    private fun showOutputWhenBoxIsConfigured(isConfigured: Boolean) {
        binding.apply {
            when(isConfigured) {
                true -> {

                }
                false -> {

                }
            }
        }
    }

}