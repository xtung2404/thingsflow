package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlayConfigOutputJsonBinding
import com.example.thingsflow.ui.OverlayBase

class OverlayConfigOutputJson(
    context: Context,
    container: ViewGroup,
    private val onClose: () -> Unit
) : OverlayBase<LayoutOverlayConfigOutputJsonBinding>(
    context,
    container,
    LayoutOverlayConfigOutputJsonBinding::inflate
) {

    override fun onViewCreated(binding: LayoutOverlayConfigOutputJsonBinding) {
        binding.apply {

        }
    }
}