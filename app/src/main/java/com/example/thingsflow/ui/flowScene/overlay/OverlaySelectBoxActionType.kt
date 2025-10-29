package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxActionTypeBinding
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxEventTypeBinding
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxTypeBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxActionType
import com.example.thingsflow.ui.adapter.AdapterBoxEventType
import com.example.thingsflow.ui.adapter.AdapterBoxType
import com.example.thingsflow.utils.getSupportedBoxEvent
import com.example.thingsflow.utils.getSupportedBoxType
import com.google.android.material.tabs.TabLayout
import rogo.iot.module.flowcommon.type.FTypeAction
import rogo.iot.module.flowcommon.type.FTypeEvent
import kotlin.getValue

class OverlaySelectBoxActionType(
    context: Context,
    container: ViewGroup,
    private val onBoxActionTypeSelected: (Int) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlaySelectBoxActionTypeBinding>(
    context,
    container,
    LayoutOverlaySelectBoxActionTypeBinding::inflate
) {
    private val adapterBoxActionType: AdapterBoxActionType by lazy {
        AdapterBoxActionType(
            onItemClicked = {
                onBoxActionTypeSelected.invoke(it)
            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlaySelectBoxActionTypeBinding) {
        binding.apply {
            rvBoxType.adapter = adapterBoxActionType
            adapterBoxActionType.submitList(
                listOf<Int>(
                    FTypeAction.ACT_CONTROL_DEVICE,
                    FTypeAction.ACT_CALL_HTTP
                )
            )

            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnCancel.setOnClickListener {
                onClose.invoke()
            }
        }
    }
}