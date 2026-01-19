package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import rogo.iot.module.flowcommon.type.FHttpType

class AdapterSpinnerMethodCallHttpType(
    context: Context,
    private val items: List<Int>
): ArrayAdapter<Int>(context, R.layout.layout_spinner_item_location, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_method_http, parent, false)

        val textView = view.findViewById<TextView>(R.id.txt_label)
        textView.text = getMethodHttpLabel(items[position])


        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            view.text = getMethodHttpLabel(items[position])
        }
        return view
    }

    fun getMethodHttpLabel(method: Int): String {
        return when (method) {
            FHttpType.GET -> context.getString(R.string.get)
            FHttpType.POST ->  context.getString(R.string.post)
            FHttpType.DELETE -> context.getString(R.string.delete)
            else -> ""
        }
    }
}