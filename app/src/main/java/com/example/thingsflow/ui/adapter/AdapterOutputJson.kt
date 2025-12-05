package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemJsonFieldBinding
import com.example.thingsflow.module.define.TFJsonField

class AdapterOutputJson(
): ListAdapter<TFJsonField, AdapterOutputJson.OutputJsonViewHolder>(
    object : DiffUtil.ItemCallback<TFJsonField>() {
        override fun areItemsTheSame(
            oldItem: TFJsonField,
            newItem: TFJsonField
        ): Boolean {
            return oldItem.id.contentEquals(newItem.id) && oldItem.label.contentEquals(newItem.label)
        }

        override fun areContentsTheSame(
            oldItem: TFJsonField,
            newItem: TFJsonField
        ): Boolean {
            return oldItem.id.contentEquals(newItem.id) && oldItem.label.contentEquals(newItem.label)
        }

    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OutputJsonViewHolder {
        val inflater = LayoutItemJsonFieldBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OutputJsonViewHolder(inflater)
    }

    override fun onBindViewHolder(
        holder: OutputJsonViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    inner class OutputJsonViewHolder(private val binding: LayoutItemJsonFieldBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: TFJsonField) {
            binding.apply {
                txtLabel.text = item.label

            }
        }
    }


}