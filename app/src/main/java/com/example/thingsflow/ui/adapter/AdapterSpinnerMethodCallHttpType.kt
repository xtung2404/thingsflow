package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingsflow.R
import com.example.thingsflow.module.model.TypeCallHttpMethod
import com.example.thingsflow.utils.getBoxEventTypeLabel
import rogo.iot.module.rogocore.sdk.entity.IoTGroup
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class AdapterSpinnerMethodCallHttpType(
    context: Context,
    private val items: List<TypeCallHttpMethod>
): ArrayAdapter<TypeCallHttpMethod>(context, R.layout.layout_spinner_item_location, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_location, parent, false)

        val textView = view.findViewById<TextView>(R.id.txt_label)
        when(items[position]) {
            TypeCallHttpMethod.GET -> textView.text = context.getString(R.string.get)
            TypeCallHttpMethod.POST -> textView.text = context.getString(R.string.post)
            TypeCallHttpMethod.DELETE -> textView.text = context.getString(R.string.delete)
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            when(items[position]) {
                TypeCallHttpMethod.GET -> view.text = context.getString(R.string.get)
                TypeCallHttpMethod.POST -> view.text = context.getString(R.string.post)
                TypeCallHttpMethod.DELETE -> view.text = context.getString(R.string.delete)
            }
        }
        return view
    }
}