package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemTableJsonFieldBinding
import com.example.thingsflow.utils.getFieldTypeLabel

class AdapterTableJsonField(

): ListAdapter<Map.Entry<String, Int>, AdapterTableJsonField.TableJsonFieldViewHolder>(DIFF) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableJsonFieldViewHolder {
        val inflater = LayoutItemTableJsonFieldBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TableJsonFieldViewHolder(inflater)
    }

    override fun onBindViewHolder(
        holder: TableJsonFieldViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    inner class TableJsonFieldViewHolder(private val binding: LayoutItemTableJsonFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(field: Map.Entry<String, Int>) {
            binding.apply {
                txtLabel.text = field.key
                txtType.text = getFieldTypeLabel(root.context, field.value)
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Map.Entry<String, Int>>() {
            override fun areItemsTheSame(
                oldItem: Map.Entry<String, Int>,
                newItem: Map.Entry<String, Int>
            ): Boolean {
                return oldItem.key.contentEquals(newItem.key) && oldItem.value == newItem.value
            }

            override fun areContentsTheSame(
                oldItem: Map.Entry<String, Int>,
                newItem: Map.Entry<String, Int>
            ): Boolean {
                return oldItem.key.contentEquals(newItem.key) && oldItem.value == newItem.value

            }
        }
    }
}