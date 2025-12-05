package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxTypeBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxType
import com.example.thingsflow.utils.getSupportedBoxType

/**
 * @file: This overlay is used to select type of box(action or condition)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxTypeSelected: triggered when type of box is selected
 * @param onClose: triggered when hide the overlay
 */
class OverlaySelectBoxType(
    context: Context,
    container: ViewGroup,
    private val onBoxTypeSelected: (Int) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlaySelectBoxTypeBinding>(
    context,
    container,
    LayoutOverlaySelectBoxTypeBinding::inflate
) {

    // adapter for select type of box
    private val adapterBoxType: AdapterBoxType by lazy {
        AdapterBoxType(
            onItemClicked = {
                onBoxTypeSelected.invoke(it)
            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlaySelectBoxTypeBinding) {
        binding.apply {
            rvBoxType.adapter = adapterBoxType
            adapterBoxType.submitList(getSupportedBoxType())

            btnCancel.setOnClickListener {
                onClose.invoke()
            }
        }
    }
}