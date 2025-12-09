package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.utils.getCmdLabel
import rogo.iot.module.base.define.IoTAttribute

class AdapterSpinnerComparingValue(
    context: Context,
    private val items: List<Pair<TFInOutType, IntArray>>
): ArrayAdapter<Pair<TFInOutType, IntArray>>(context, R.layout.layout_spinner_item_location, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_location, parent, false)

        val textView = view.findViewById<TextView>(R.id.txt_label)
        val comparingValueType = items[position].first
        val comparingValue = items[position].second
        comparingValue.let {
            if (comparingValue.isNotEmpty() && comparingValue.size >= 2) {
                when(comparingValueType) {
                    TFInOutType.PAYLOAD -> {
                        when(comparingValue[0]) {
                            IoTAttribute.ACT_ONOFF -> {
                                textView.text = getCmdLabel(context, comparingValue[0], comparingValue[1])
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        val comparingValueType = items[position].first
        val comparingValue = items[position].second
        comparingValue.let {
            if (comparingValue.isNotEmpty() && comparingValue.size >= 2) {
                when(comparingValueType) {
                    TFInOutType.PAYLOAD -> {
                        when(comparingValue[0]) {
                            IoTAttribute.ACT_ONOFF -> {
                                view.text = getCmdLabel(context, comparingValue[0], comparingValue[1])
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
        return view
    }
}