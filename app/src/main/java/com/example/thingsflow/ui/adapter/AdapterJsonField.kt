package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemJsonFieldBinding
import com.example.thingsflow.module.define.TFFieldType
import com.example.thingsflow.module.define.TFJsonField
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show

class AdapterJsonField(
    private val onMenuClick: () -> Unit
): ListAdapter<TFJsonField, AdapterJsonField.JsonFieldViewHolder>(DIFF) {
    inner class JsonFieldViewHolder(private val binding: LayoutItemJsonFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(field: TFJsonField) {
            binding.apply {
                txtLabel.text = field.label
                txtType.text = TFFieldType.getFieldTypeLabel(root.context, field.type)
                btnMenu.setOnClickListener {
                    onMenuClick.invoke()
                }

                when(field.type) {
                    TFFieldType.OBJECT -> {
                        rvField.show()
                        val adapterJsonField: AdapterJsonField = AdapterJsonField(
                            onMenuClick = {
//                                showMenuForField(field, adapterJsonField)
                            }
                        )
                        rvField.adapter = adapterJsonField
                        adapterJsonField.submitList(field.fields)
                    }
                    else -> {
                        rvField.gone()
                    }
                }
            }
        }
//        private fun showMenuForField(field: TFJsonField, adapter: AdapterJsonField) {
//            val popup = PopupMenu(binding.root.context, binding.btnMenu)
//            popup.menuInflater.inflate(R.menu.menu_json_field, popup.menu)
//
//            popup.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.addField -> {
//                        val newField = TFJsonField(label = "newField", type = TFFieldType.NUMBER)
//                        field.fields.add(newField)
//
//                        adapter.submitList(field.fields.toList())
//                        true
//                    }
//                    else -> false
//                }
//            }
//
//            popup.show()
//        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JsonFieldViewHolder {
        val inflater = LayoutItemJsonFieldBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JsonFieldViewHolder(inflater)
    }

    override fun onBindViewHolder(
        holder: JsonFieldViewHolder,
        position: Int
    ) {
        holder.onBind(getItem(position))
    }


    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TFJsonField>() {
            override fun areItemsTheSame(
                oldItem: TFJsonField,
                newItem: TFJsonField
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TFJsonField,
                newItem: TFJsonField
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.label.contentEquals(newItem.label)
            }
        }
    }
}