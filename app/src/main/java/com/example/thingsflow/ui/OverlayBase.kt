package com.example.thingsflow.ui

import android.content.Context
import android.text.Layout
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import com.example.thingsflow.R

abstract class OverlayBase<VB: ViewBinding>(
    protected val context: Context,
    protected val container: ViewGroup,
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected val viewModelOwner: ViewModelStoreOwner? by lazy {
        when (context) {
            is ViewModelStoreOwner -> context
            is androidx.fragment.app.FragmentActivity -> context
            else -> null
        }
    }

    private var view: View?= null

    open fun show() {
        if (view == null) {
            _binding = bindingInflater(LayoutInflater.from(context), container, false)
            view = binding.root
            container.addView(view)
            onViewCreated(binding)
        }
        handleOverlayAnimation(isOpen = true)
    }

    fun hide() {
        handleOverlayAnimation(isOpen = false)
    }

    private fun handleOverlayAnimation(isOpen: Boolean) {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val v = view ?: return

        if (isOpen) {
            v.visibility = View.VISIBLE
            v.translationX = screenWidth
            v.animate()
                .translationX(0f)
                .setDuration(300)
                .start()
        } else {
            v.animate()
                .translationX(screenWidth)
                .setDuration(300)
                .withEndAction {
                    v.visibility = View.GONE
                    v.translationX = 0f
                }
                .start()
        }
    }

    protected abstract fun onViewCreated(binding: VB)


}