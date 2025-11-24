package com.example.thingsflow.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemHeaderBinding
import com.example.thingsflow.module.model.ItemHeader

class AdapterHeader() : ListAdapter<ItemHeader, AdapterHeader.HeaderViewHolder>(
    object : DiffUtil.ItemCallback<ItemHeader>() {
        override fun areItemsTheSame(
            oldItem: ItemHeader,
            newItem: ItemHeader
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: ItemHeader,
            newItem: ItemHeader
        ): Boolean {
            return false
        }

    }
) {
    inner class HeaderViewHolder(private val binding: LayoutItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ItemHeader) {
            binding.apply {
                edtKey.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        currentList.find { it.id == item.id }?.key = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })

                edtValue.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        currentList.find { it.id == item.id }?.value = s.toString()

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        item.value = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })
            }
        }
    }

    override fun onBindViewHolder(
        holder: HeaderViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeaderViewHolder {
        val inflater = LayoutItemHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderViewHolder(inflater)
    }
}