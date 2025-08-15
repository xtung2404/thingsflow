package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import com.example.thingsflow.utils.getBoxEventTypeLabel
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

class AdapterSpinnerBoxEventType(
    context: Context,
    private val items: List<Int>
): ArrayAdapter<Int>(context, android.R.layout.simple_spinner_item, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = getBoxEventTypeLabel(context, items[position])
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = getBoxEventTypeLabel(context, items[position])
        return view
    }
}