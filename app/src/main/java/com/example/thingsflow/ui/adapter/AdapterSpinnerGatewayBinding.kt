package com.example.thingsflow.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import rogo.iot.module.rogocore.sdk.entity.IoTGroup

class AdapterSpinnerGatewayBinding(
    context: Context,
    private val items: List<Map.Entry<String?, IntArray>>
): ArrayAdapter<Map.Entry<String?, IntArray>>(context, R.layout.layout_spinner_item_gateway_binding, items) {
    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_spinner_item_gateway_binding, parent, false)

        val txtLabel = view.findViewById<TextView>(R.id.txt_label)
        val txtLocation = view.findViewById<TextView>(R.id.txt_location)
        val txtSpot = view.findViewById<TextView>(R.id.txt_spot)

        val device = FlowSdk.deviceHandler().get(items[position].key)
        device?.let {
            txtLabel.text = device.label
            val location = FlowSdk.locationHandler().get(device.locationId)
            location?.let {
                txtLocation.text = location.label
            }
            txtSpot.text = "${position + 1}/${items.size}"
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        if (items[position] == null) {
            view.text = context.resources.getString(R.string.undefined)
        } else {
            val device = FlowSdk.deviceHandler().get(items[position].key)
            device?.let {
                view.text = device.label
//                val location = FlowSdk.locationHandler().get(device.locationId)
//                location?.let {
//                    txtLocation.text = location.label
//                }
//                txtSpot.text = "$position / ${items.size}"
            }
        }
        return view
    }
}