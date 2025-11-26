package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxEventTypeBinding
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxTypeBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxEventType
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.type.FTypeEvent

/**
 * @file: This overlay is used to select type of box event(from device, etc...)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxEventTypeSelected: triggered when type of box event is selected
 * @param onClose: triggered when hide the overlay
 */
class OverlaySelectBoxEventType(
    context: Context,
    container: ViewGroup,
    private val onBoxEventTypeSelected: (Int) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlaySelectBoxEventTypeBinding>(
    context,
    container,
    LayoutOverlaySelectBoxEventTypeBinding::inflate
) {
    // adapter for select type of box event
    private val adapterBoxEventType: AdapterBoxEventType by lazy {
        AdapterBoxEventType(
            onItemClicked = {
                onBoxEventTypeSelected.invoke(it)
            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlaySelectBoxEventTypeBinding) {
        binding.apply {
            rvBoxEventType.adapter = adapterBoxEventType
            adapterBoxEventType.submitList(getSupportedBoxEvent())

            btnCancel.setOnClickListener {
                onClose.invoke()
            }
        }
    }
}