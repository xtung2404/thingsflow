package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemInputBinding
import com.example.thingsflow.module.define.TFInputType
import com.example.thingsflow.utils.getAttrLabel
import com.example.thingsflow.utils.getDeviceTypeLabel
import com.example.thingsflow.utils.getInputLabel

class AdapterInput: ListAdapter<Pair<TFInputType, Int> , AdapterInput.InputViewHolder>(
    object : DiffUtil.ItemCallback<Pair<TFInputType, Int>>() {
        override fun areItemsTheSame(
            oldItem: Pair<TFInputType, Int>,
            newItem: Pair<TFInputType, Int>
        ): Boolean {
            return oldItem.first == newItem.first && oldItem.second == newItem.second
        }

        override fun areContentsTheSame(
            oldItem: Pair<TFInputType, Int>,
            newItem: Pair<TFInputType, Int>
        ): Boolean {
            return oldItem.first == newItem.first && oldItem.second == newItem.second
        }
    }

) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InputViewHolder {
        val inflater = LayoutItemInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InputViewHolder(
            inflater
        )
    }

    override fun onBindViewHolder(
        holder: InputViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class InputViewHolder(private val binding: LayoutItemInputBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(inputEntry: Pair<TFInputType, Int>) {
            binding.apply {
                txtInput.text = getInputLabel(root.context, inputEntry)
            }
        }
    }
}