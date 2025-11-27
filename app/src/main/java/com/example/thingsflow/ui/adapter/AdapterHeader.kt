package com.example.thingsflow.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemHeaderBinding
import com.example.thingsflow.module.define.TFItemHeader

class AdapterHeader(
    private val onDelete: (Int, TFItemHeader) -> Unit
) :
    ListAdapter<TFItemHeader, AdapterHeader.HeaderViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TFItemHeader>() {
            override fun areItemsTheSame(a: TFItemHeader, b: TFItemHeader) = a.id == b.id

            override fun areContentsTheSame(a: TFItemHeader, b: TFItemHeader) =
                a.key == b.key && a.value == b.value
        }
    }

    inner class HeaderViewHolder(
        val binding: LayoutItemHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var keyWatcher: TextWatcher? = null
        private var valueWatcher: TextWatcher? = null

        fun bind(item: TFItemHeader) = binding.run {
            keyWatcher?.let { edtKey.removeTextChangedListener(it) }
            valueWatcher?.let { edtValue.removeTextChangedListener(it) }

            keyWatcher = object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    currentList.find { it.id == item.id }?.key = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            valueWatcher = object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    currentList.find { it.id == item.id }?.value = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            btnDelete.setOnClickListener {
                onDelete.invoke(position, item)
            }

            if (edtKey.text.toString() != item.key) {
                edtKey.setText(item.key ?: "")
            }
            if (edtValue.text.toString() != item.value) {
                edtValue.setText(item.value ?: "")
            }
            edtKey.addTextChangedListener(keyWatcher)
            edtValue.addTextChangedListener(valueWatcher)
        }
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = LayoutItemHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderViewHolder(binding)
    }
}
