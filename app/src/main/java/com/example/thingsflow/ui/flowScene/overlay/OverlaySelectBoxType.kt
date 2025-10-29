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
import com.example.thingsflow.ui.adapter.AdapterBoxType
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.example.thingsflow.utils.getSupportedBoxType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.type.FTypeEvent
import kotlin.getValue

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