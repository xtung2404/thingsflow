package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemBoxActionTypeBinding
import com.example.thingsflow.ui.adapter.AdapterBoxActionType.BoxActionTypeViewHolder
import rogo.iot.module.flowcommon.type.FBoxType

/**
 * adapter for selecting type of the next action box(control device or call http or ....)
 *
 */
class AdapterBoxActionType(
    private val onItemClicked: (Int) -> Unit
): ListAdapter<Int, BoxActionTypeViewHolder> (
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
    ): BoxActionTypeViewHolder {
        val inflater = LayoutItemBoxActionTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BoxActionTypeViewHolder(
            inflater
        )
    }

    override fun onBindViewHolder(
        holder: BoxActionTypeViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class BoxActionTypeViewHolder(private val binding: LayoutItemBoxActionTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(boxActionType: Int) {
            binding.apply {
                when (boxActionType) {
                    FBoxType.ACT_CONTROL_DEVICE -> {
                        txtLabel.text = root.context.getString(R.string.control_device)
                        txtDesc.text = root.context.getString(R.string.config_control_one_device_or_a_group)
                    }

                    FBoxType.ACT_CALL_HTTP -> {
                        txtLabel.text = root.context.getString(R.string.call_http)
                        txtDesc.text = root.context.getString(R.string.config_connection_through_api)
                    }

                    FBoxType.ACT_CONDITION_GENERAL -> {
                        txtLabel.text = root.context.getString(R.string.condition_general)

                    }
                    FBoxType.ACT_CONDITION_DEVICE -> {
                        txtLabel.text = root.context.getString(R.string.state_from_device)

                    }
                }

                root.setOnClickListener {
                    onItemClicked.invoke(boxActionType )
                }
            }
        }
    }
}