package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemAttributeBinding
import com.example.thingsflow.databinding.LayoutItemDiscoveredDeviceBinding
import com.example.thingsflow.utils.getAttrLabel
import rogo.iot.module.platform.entity.IoTDirectDeviceInfo

class AdapterAttributes(
    val onItemClicked: (Int) -> Unit
):
ListAdapter<Map.Entry<Int, Boolean>, AdapterAttributes.AttributesViewHolder>(
    object : DiffUtil.ItemCallback<Map.Entry<Int, Boolean>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<Int, Boolean>,
            newItem: Map.Entry<Int, Boolean>
        ): Boolean {
            return oldItem.key == newItem.key && oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<Int, Boolean>,
            newItem: Map.Entry<Int, Boolean>
        ): Boolean {
            return oldItem.key == newItem.key && oldItem.value == newItem.value
        }
    }
) {
    inner class AttributesViewHolder(
        private val binding: LayoutItemAttributeBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(attr: Map.Entry<Int, Boolean>) {
            binding.apply {
                txtLabel.text = getAttrLabel(binding.root.context, attr.key)
                if (attr.value) {
                    root.setBackgroundResource(R.drawable.bg_white_stroke_emerald)
                } else {
                    root.setBackgroundResource(R.drawable.bg_white)
                }
                root.setOnClickListener {
                    onItemClicked.invoke(attr.key)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributesViewHolder {
        val inflater = LayoutItemAttributeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttributesViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: AttributesViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}