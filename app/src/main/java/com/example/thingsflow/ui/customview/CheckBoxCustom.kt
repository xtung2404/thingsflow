package com.example.thingsflow.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.thingsflow.R

class CheckBoxCustom  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: View, isChecked: Boolean)
    }

    private var listener: OnCheckedChangeListener? = null

    var isChecked = false
        set(value) {
            if (field == value) return
            field = value
            refreshDrawableState()
            listener?.onCheckedChanged(this, value)
        }

    init {
        setImageResource(R.drawable.custom_checkbox_emerald)
        isClickable = true
        isFocusable = true

        setOnClickListener {
            toggle()
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val state = super.onCreateDrawableState(extraSpace + 1)

        if (isChecked) {
            View.mergeDrawableStates(state, CHECKED_STATE)
        }
        return state
    }

    fun toggle() {
        isChecked = !isChecked
    }

    fun setOnCheckedChangeListener(l: OnCheckedChangeListener?) {
        listener = l
    }

    fun setOnCheckedChangeListener(listener: (View, Boolean) -> Unit) {
        this.listener = object : OnCheckedChangeListener {
            override fun onCheckedChanged(view: View, isChecked: Boolean) {
                listener(view, isChecked)
            }
        }
    }

    companion object {
        private val CHECKED_STATE = intArrayOf(android.R.attr.state_checked)
    }
}