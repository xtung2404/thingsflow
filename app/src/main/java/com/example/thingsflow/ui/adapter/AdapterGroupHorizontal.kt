package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemGroupHorizontalBinding
import com.example.thingsflow.databinding.LayoutItemLocationManagementBinding
import rogo.iot.module.rogocore.sdk.entity.IoTGroup
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

class AdapterGroupHorizontal(
    private val onItemClick: (IoTGroup?) -> Unit
) : ListAdapter<IoTGroup, AdapterGroupHorizontal.GroupViewHolder>(
    object: DiffUtil.ItemCallback<IoTGroup?>() {
        override fun areItemsTheSame(
            oldItem: IoTGroup,
            newItem: IoTGroup
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: IoTGroup,
            newItem: IoTGroup
        ): Boolean {
            return false
        }
    }
) {
    private var selectedGroupId: String? = null

    inner class GroupViewHolder(
        private val binding: LayoutItemGroupHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: IoTGroup?, isSelected: Boolean) {
            binding.apply {
                txtLabel.text = if (group == null) root.context.getString(R.string.all) else group.label
                if (isSelected) {
                    root.setBackgroundResource(R.drawable.bg_gray_stroke_emerald)
                } else {
                    root.setBackgroundColor(root.context.getColor(R.color.light_gray_10))
                }

                root.setOnClickListener {
                    if (selectedGroupId != group?.uuid) {
                        selectedGroupId = group?.uuid
                        onItemClick.invoke(group)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutItemGroupHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val item = getItem(position)
        val isSelected = item?.uuid == selectedGroupId
        holder.bind(item, isSelected)
    }

//    fun setSelectedLocation(loc: IoTLocation) {
//        selectedLocationId = loc.uuid
//        notifyDataSetChanged()
//    }
//
//    fun setSelectedLocation(uuid: String) {
//        selectedLocationId = uuid
//        notifyDataSetChanged()
//    }

    fun getSelectedGroup(): IoTGroup? {
        return currentList.find { it.uuid == selectedGroupId }
    }
}