package com.example.thingsflow.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class DialogBase<B: ViewDataBinding> (
    context: Context,
    @LayoutRes private val layoutResId: Int,
    private val cancelable: Boolean = false
): Dialog(context) {
    protected lateinit var binding: B
        private set

    init {
        initDialog()
    }

    private fun initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, layoutResId, null, false)
        setContentView(binding.root)
        setCancelable(cancelable)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setOnShowListener {
            onDialogShown()
        }
        setupView(binding)
    }

    protected abstract fun setupView(binding: B)

    open fun onDialogShown() {

    }
    fun showDialog() {
        if (context is Context && (context !is Activity || !(context as Activity).isFinishing)) {
            show()
        }
    }
}