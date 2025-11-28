package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import rogo.iot.module.base.define.IoTAttribute

class AdapterSpinnerControlAction(
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
        textView.text = getControlActionLabel(items[position])

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            view.text = getControlActionLabel(items[position])
        }
        return view
    }

    fun getControlActionLabel(action: Int): String {
        when(action) {
            IoTAttribute.ACT_ONOFF -> return context.getString(R.string.on_off)
            IoTAttribute.ACT_LOCK_UNLOCK -> return context.getString(R.string.lock_unlock)
            IoTAttribute.ACT_OPEN_CLOSE -> return context.getString(R.string.open_close)
            else -> ""
        }
        return ""
    }
}