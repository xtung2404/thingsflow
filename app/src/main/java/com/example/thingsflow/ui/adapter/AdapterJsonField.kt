package com.example.thingsflow.ui.adapter

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemJsonFieldBinding
import com.example.thingsflow.utils.getFieldTypeColor
import com.example.thingsflow.utils.getFieldTypeLabel
import com.example.thingsflow.utils.gone
import com.example.thingsflow.utils.show
import rogo.iot.module.flowcommon.define.FJsonField
import rogo.iot.module.flowcommon.type.FInputValueType

class AdapterJsonField(
    private val onMenuClick: (parentField: FJsonField, returnToChild: (FJsonField) -> Unit) -> Unit,
    private val onNotifyParent: () -> Unit // Callback báo lên root
) : ListAdapter<FJsonField, AdapterJsonField.JsonFieldViewHolder>(DIFF) {

    inner class JsonFieldViewHolder(private val binding: LayoutItemJsonFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Khởi tạo adapter con cố định cho mỗi ViewHolder
        private val childAdapter = AdapterJsonField(onMenuClick, {
            // Khi cấp cháu đổi, thằng con đo đạc lại chính mình
            notifyItemChanged(adapterPosition)
            // Và báo tiếp lên thằng cha Overlay
            onNotifyParent.invoke()
        })

        init {
            binding.rvField.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = childAdapter
                // Tắt NestedScrolling để RecyclerView con nở theo Wrap_Content
                isNestedScrollingEnabled = false
            }
        }

        fun onBind(field: FJsonField) {
            binding.apply {
                setUpView(field)

                btnMenu.setOnClickListener {
                    onMenuClick.invoke(field) { newField ->
                        val newFields = field.fields + newField
                        field.fields = newFields

                        childAdapter.submitList(field.fields.toList())

                        notifyItemChanged(adapterPosition)

                        onNotifyParent.invoke()
                    }
                }

                root.setOnClickListener {
                    if (field.type == FInputValueType.OBJECT) {
                        if (rvField.isShown) {
                            rvField.gone()
                            btnExpand.setImageDrawable(root.context.getDrawable(R.drawable.ic_forward_full))
                        } else {
                            rvField.show()
                            btnExpand.setImageDrawable(root.context.getDrawable(R.drawable.ic_downward_full))
                        }
                    }
                }
            }
        }
        private fun setUpView(field: FJsonField) {
            binding.apply {
                txtLabel.text = field.label
                txtType.text = getFieldTypeLabel(root.context, field.type)
                txtType.setTextColor(getFieldTypeColor(root.context, field.type))

                var imgDrawable: Drawable?= null
                var imgTint: Int?= null

                when(field.type) {
                    FInputValueType.OBJECT -> {
                        imgDrawable = root.context.getDrawable(R.drawable.ic_up)
                        imgTint = R.color.black
                        rvField.show()
                        childAdapter.submitList(field.fields.toList())
                    }
                    else -> {
                        imgDrawable = root.context.getDrawable(R.drawable.ic_forward_full)
                        imgTint = R.color.gray
                        rvField.gone()
                        childAdapter.submitList(null)
                    }
                }

                imgDrawable?.let { btnExpand.setImageDrawable(imgDrawable) }
                imgTint.let {
                    btnExpand.setColorFilter(
                        ContextCompat.getColor(root.context, imgTint),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JsonFieldViewHolder {
        return JsonFieldViewHolder(
            LayoutItemJsonFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: JsonFieldViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<FJsonField>() {
            override fun areItemsTheSame(old: FJsonField, new: FJsonField) = old.uuid == new.uuid
            override fun areContentsTheSame(old: FJsonField, new: FJsonField) = false
        }
    }
}