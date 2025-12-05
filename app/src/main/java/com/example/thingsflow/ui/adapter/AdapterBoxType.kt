package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemBoxTypeBinding
import com.example.thingsflow.utils.FTypeBox

class AdapterBoxType(
    private val onItemClicked: (Int) -> Unit
): ListAdapter<Int, AdapterBoxType.BoxTypeViewHolder>(
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
    ): BoxTypeViewHolder {
        val inflater = LayoutItemBoxTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BoxTypeViewHolder(
            inflater
        )
    }

    override fun onBindViewHolder(
        holder: BoxTypeViewHolder,
        position: Int
    ) {
        holder.bindData(getItem(position))
    }

    inner class BoxTypeViewHolder(private val binding: LayoutItemBoxTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(boxType: Int) {
            binding.apply {
                when (boxType) {
                    FTypeBox.TYPE_BOX_EVENT -> {

                    }
                    FTypeBox.TYPE_BOX_ACTION -> {
                        txtLabel.text = root.context.getString(R.string.action)
                        txtDesc.text = root.context.getString(R.string.configing_helps_flow_create_action)
                        imgStatus.setImageDrawable(root.context.getDrawable(R.drawable.ic_box_act))
                    }
                    FTypeBox.TYPE_BOX_CONDITION -> {
                        txtLabel.text = root.context.getString(R.string.condition)
                        txtDesc.text = root.context.getString(R.string.configing_helps_flow_create_condition)
                        imgStatus.setImageDrawable(root.context.getDrawable(R.drawable.ic_box_cdt))
                    }
                }

                root.setOnClickListener {
                    onItemClicked.invoke(boxType)
                }
            }
        }
    }
}