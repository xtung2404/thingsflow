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

class AdapterInput(
    private val onItemClicked: (Int) -> Unit
): ListAdapter<Pair<TFInputType, Int> , AdapterInput.InputViewHolder>(
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
                when(inputEntry.first) {
                    TFInputType.DEVICE_TYPE -> {
                        txtInput.text = getDeviceTypeLabel(root.context, inputEntry.second)
                    }
                    TFInputType.ATTRIBUTE -> {
                        txtInput.text = getAttrLabel(root.context, inputEntry.second)
                    }
                    TFInputType.PAYLOAD -> {
                        txtInput.text = getAttrLabel(root.context, inputEntry.second)
                    }
                }
            }
        }
    }
}