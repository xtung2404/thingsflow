package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemAttributeBinding
import com.example.thingsflow.utils.getAttrLabel

class AdapterAttributes(
    val onItemClicked: (Pair<Int, String>) -> Unit
):
ListAdapter<MutableMap.MutableEntry<Pair<Int, String>, Boolean>, AdapterAttributes.AttributesViewHolder>(
    object : DiffUtil.ItemCallback<MutableMap.MutableEntry<Pair<Int, String>, Boolean>>() {
        override fun areItemsTheSame(
            oldItem: MutableMap.MutableEntry<Pair<Int, String>, Boolean>,
            newItem: MutableMap.MutableEntry<Pair<Int, String>, Boolean>
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: MutableMap.MutableEntry<Pair<Int, String>, Boolean>,
            newItem: MutableMap.MutableEntry<Pair<Int, String>, Boolean>
        ): Boolean {
            return false
        }
    }
) {
    inner class AttributesViewHolder(
        private val binding: LayoutItemAttributeBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(attr: MutableMap.MutableEntry<Pair<Int, String>, Boolean>) {
            binding.apply {
                txtLabel.text = attr.key.second
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