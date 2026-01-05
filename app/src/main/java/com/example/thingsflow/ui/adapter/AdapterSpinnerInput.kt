package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFInputBoxValue
import com.example.thingsflow.utils.getInputLabel

class AdapterSpinnerInput(
    context: Context,
    private val items: List<TFInputBoxValue>
): ArrayAdapter<TFInputBoxValue>(context, R.layout.layout_spinner_item_compared_value, items) {
    init {
        setDropDownViewResource(R.layout.layout_spinner_item_compared_value)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_compared_value, parent, false)

        val txtElm = view.findViewById<TextView>(R.id.txt_elm)
        val txtState = view.findViewById<TextView>(R.id.txt_label)
        val inputValue = items[position]
        val device = FlowSdk.deviceHandler().get(inputValue.devId)
        device?.let {
            txtElm.text = device.elementInfos[inputValue.elm]?.label?: "Nút ${inputValue.elm}"
            txtState.text =  getInputLabel(context, inputValue)
        }


        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_compared_value, parent, false)

        val txtElm = view.findViewById<TextView>(R.id.txt_elm)
        val txtState = view.findViewById<TextView>(R.id.txt_label)
        val inputValue = items[position]
        val device = FlowSdk.deviceHandler().get(inputValue.devId)
        device?.let {
            txtElm.text = device.elementInfos[inputValue.elm]?.label?: "Nút ${inputValue.elm}"
            txtState.text =  getInputLabel(context, inputValue)
        }


        return view
    }
}