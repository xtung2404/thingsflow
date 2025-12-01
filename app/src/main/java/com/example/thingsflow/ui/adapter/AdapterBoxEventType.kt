package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemBoxEventTypeBinding
import rogo.iot.module.flowcommon.type.FBoxType

class AdapterBoxEventType(
    private val onItemClicked: (Int) -> Unit
): ListAdapter<Int, AdapterBoxEventType.BoxEventTypeViewHolder>(
    object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoxEventTypeViewHolder {
        val inflater = LayoutItemBoxEventTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BoxEventTypeViewHolder(
            inflater
        )
    }

    override fun onBindViewHolder(
        holder: BoxEventTypeViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class BoxEventTypeViewHolder(private val binding: LayoutItemBoxEventTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(boxEvtType: Int) {
            binding.apply {
                when (boxEvtType) {
                    FBoxType.EVT_FROM_DEVICE -> {
                        txtLabel.text = root.context.getString(R.string.event_from_device)
                        txtDesc.text = root.context.getString(R.string.receive_event_from_selected_device)
                        imgStatus.setImageDrawable(root.context.getDrawable(R.drawable.ic_evt_device))
                    }
                }

                root.setOnClickListener {
                    onItemClicked.invoke(boxEvtType)
                }
            }
        }
    }
}