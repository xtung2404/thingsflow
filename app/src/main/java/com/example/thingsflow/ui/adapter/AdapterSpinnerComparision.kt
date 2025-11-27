package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingflowsdk.core.define.TFComparision
import com.example.thingflowsdk.core.define.TFMethodHttp
import com.example.thingsflow.R
import rogo.iot.module.platform.define.IoTCondition

class AdapterSpinnerComparision(
    context: Context,
    private val items: List<Int>
): ArrayAdapter<Int>(context, R.layout.layout_spinner_item_location, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_location, parent, false)

        val textView = view.findViewById<TextView>(R.id.txt_label)
        textView.text = getComparisionLabel(items[position])


        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            view.text = getComparisionLabel(items[position])
        }
        return view
    }

    fun getComparisionLabel(comparision: Int): String {
        return when (comparision) {
            TFComparision.EQUAL -> context.getString(R.string.compare_equal)
            TFComparision.DIFF -> context.getString(R.string.compare_diff)
            TFComparision.BETWEEN -> context.getString(R.string.compare_between)
            TFComparision.GREATER_THAN -> context.getString(R.string.compare_greater_than)
            TFComparision.GREATER_EQUAL -> context.getString(R.string.compare_greater_equal)
            TFComparision.LESS_THAN -> context.getString(R.string.compare_less_than)
            TFComparision.LESS_EQUAL -> context.getString(R.string.compare_less_equal)
            else -> ""
        }
    }
}