package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.databinding.LayoutItemHeaderElementBinding
import com.example.thingsflow.databinding.LayoutItemInputStateBinding
import com.example.thingsflow.databinding.LayoutItemTableJsonFieldBinding
import com.example.thingsflow.module.define.TFInOutType
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_CONTENT_CALL_HTTP
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_CONTENT_STATE
import com.example.thingsflow.module.define.TFViewHolderType.Companion.TYPE_HEADER
import com.example.thingsflow.utils.getInputLabel

class AdapterInOutput : ListAdapter<AdapterItem, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(
            oldItem: AdapterItem,
            newItem: AdapterItem
        ): Boolean {
            return if (oldItem is AdapterItem.HeaderItem && newItem is AdapterItem.HeaderItem) {
                oldItem.title == newItem.title
            } else if (oldItem is AdapterItem.ContentItem && newItem is AdapterItem.ContentItem) {
                oldItem.data.devId == newItem.data.devId && oldItem.data.inputType == newItem.data.inputType
            } else false
        }

        override fun areContentsTheSame(
            oldItem: AdapterItem,
            newItem: AdapterItem
        ): Boolean {
            return if (oldItem is AdapterItem.HeaderItem && newItem is AdapterItem.HeaderItem) {
                oldItem.title == newItem.title
            } else if (oldItem is AdapterItem.ContentItem && newItem is AdapterItem.ContentItem) {
                oldItem.data.devId == newItem.data.devId
                        && oldItem.data.inputType == newItem.data.inputType
                        && oldItem.data.elm == newItem.data.elm
            } else false
        }
    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return (if (viewType == TYPE_HEADER) {
            val binding = LayoutItemHeaderElementBinding.inflate(inflater, parent, false)
            HeaderViewHolder(binding)
        } else if (viewType == TYPE_CONTENT_CALL_HTTP) {
            val binding = LayoutItemTableJsonFieldBinding.inflate(inflater, parent, false)
            InputHttpViewHolder(binding)
        } else {
            val binding = LayoutItemInputStateBinding.inflate(inflater, parent, false)
            InputStateViewHolder(binding)
        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is AdapterItem.ContentItem) {
            if ((getItem(position) as AdapterItem.ContentItem).data.inputType == TFInOutType.PAYLOAD_STATE) {
                TYPE_CONTENT_STATE
            } else {
                TYPE_CONTENT_CALL_HTTP
            }
        } else {
            TYPE_HEADER
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if (holder is HeaderViewHolder && item is AdapterItem.HeaderItem) {
            holder.bind(item)
        } else if (holder is InputStateViewHolder && item is AdapterItem.ContentItem) {
            holder.bindData(item)
        } else {

            (holder as InputHttpViewHolder).bindData(item as AdapterItem.ContentItem)
        }
    }

    inner class HeaderViewHolder(private val binding: LayoutItemHeaderElementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: AdapterItem.HeaderItem) {
            binding.txtLabel.text = header.title
        }
    }

    inner class InputStateViewHolder(private val binding: LayoutItemInputStateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(inputEntry: AdapterItem.ContentItem) {
            binding.apply {
                txtInput.text = getInputLabel(root.context, inputEntry.data)
            }
        }
    }

    inner class InputHttpViewHolder(private val binding: LayoutItemTableJsonFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(inputEntry: AdapterItem.ContentItem) {
            binding.apply {
                txtLabel.text = inputEntry.data.value as String
            }
        }
    }
}