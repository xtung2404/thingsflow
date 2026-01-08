package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.utils.getFieldTypeColor
import com.example.thingsflow.utils.getFieldTypeLabel
import com.example.thingsflow.utils.getInputLabel
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show

class AdapterSpinnerInput(
    context: Context,
    private val items: List<TFInputBoxValue?>
): ArrayAdapter<TFInputBoxValue>(context, R.layout.layout_spinner_item_compared_value, items) {
    init {
        setDropDownViewResource(R.layout.layout_spinner_item_compared_value)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_compared_value, parent, false)
        val inputValue = items[position]
        val lnDevElm = view.findViewById<LinearLayout>(R.id.ln_dev_elm)
        val lnJsonField = view.findViewById<LinearLayout>(R.id.ln_json_field)
        val txtFieldLabel = view.findViewById<TextView>(R.id.txt_field_label)
        val txtFieldType = view.findViewById<TextView>(R.id.txt_field_type)
        if (inputValue == null) {
            lnJsonField.show()
            lnDevElm.gone()
            txtFieldLabel.text = "Không khả dụng"
            txtFieldType.text = ""
        }
        inputValue?.let {
            when(inputValue.inputType) {
                TFInOutType.JSON_FIELD -> {
                    lnJsonField.show()
                    lnDevElm.gone()
                    txtFieldLabel.text = inputValue.input.value as String
                    txtFieldType.setTextColor(getFieldTypeColor(context, inputValue.input.type))
                    txtFieldType.text = getFieldTypeLabel(context, inputValue.input.type)
                }
                else -> {
                    lnJsonField.gone()
                    lnDevElm.show()
                    val txtElm = view.findViewById<TextView>(R.id.txt_elm)
                    val txtState = view.findViewById<TextView>(R.id.txt_label)
                    val device = FlowSdk.deviceHandler().get(inputValue.devId)
                    device?.let {
                        txtElm.text = device.elementInfos[inputValue.elm]?.label?: "Nút ${inputValue.elm}"
                        txtState.text =  getInputLabel(context, inputValue)
                    }
                }
            }
        }



        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_compared_value, parent, false)

        val inputValue = items[position]
        val lnDevElm = view.findViewById<LinearLayout>(R.id.ln_dev_elm)
        val lnJsonField = view.findViewById<LinearLayout>(R.id.ln_json_field)
        val txtFieldLabel = view.findViewById<TextView>(R.id.txt_field_label)
        val txtFieldType = view.findViewById<TextView>(R.id.txt_field_type)
        if (inputValue == null) {
            lnJsonField.show()
            lnDevElm.gone()
            txtFieldLabel.text = "Không khả dụng"
            txtFieldType.text = ""
        }
        inputValue?.let {
            when(inputValue.inputType) {
                TFInOutType.JSON_FIELD -> {
                    lnJsonField.show()
                    lnDevElm.gone()
                    txtFieldLabel.text = inputValue.input.value as String
                    txtFieldType.setTextColor(getFieldTypeColor(context, inputValue.input.type))
                    txtFieldType.text = getFieldTypeLabel(context, inputValue.input.type)
                }
                else -> {
                    lnJsonField.gone()
                    lnDevElm.show()
                    val txtElm = view.findViewById<TextView>(R.id.txt_elm)
                    val txtState = view.findViewById<TextView>(R.id.txt_label)
                    val device = FlowSdk.deviceHandler().get(inputValue.devId)
                    device?.let {
                        txtElm.text = device.elementInfos[inputValue.elm]?.label?: "Nút ${inputValue.elm}"
                        txtState.text =  getInputLabel(context, inputValue)
                    }
                }
            }
        }



        return view
    }
}