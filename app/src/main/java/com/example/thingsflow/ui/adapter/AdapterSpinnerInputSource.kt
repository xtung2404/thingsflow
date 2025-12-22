package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import com.example.thingsflow.module.define.TFInputSource

class AdapterSpinnerInputSource(
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
        textView.text = getInputSourceLabel(items[position])


        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            view.text = getInputSourceLabel(items[position])
        }
        return view
    }

    fun getInputSourceLabel(source: Int): String {
        return when (source) {
            TFInputSource.INPUT_FROM_PREVIOUS_BOX -> context.getString(R.string.input_from_previous_box)
            TFInputSource.INPUT_FROM_OTHER_DEVICES -> context.getString(R.string.input_from_new_device)

            else -> ""
        }
    }
}