package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import com.example.thingsflow.utils.getBoxEventTypeLabel
import com.example.thingsflow.utils.getDeviceTypeLabel
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

class AdapterSpinnerDeviceType(
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
        val item = items[position]

        textView.text = getDeviceTypeLabel(context, item) ?: context.getString(R.string.please_select_device_type)

        return view
    }

    override fun isEnabled(position: Int): Boolean {
        if (position == 0) return false
        return super.isEnabled(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            view.text = getDeviceTypeLabel(context, items[position]) ?: context.getString(R.string.please_select_device_type)
        }
        return view
    }
}