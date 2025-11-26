package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxConditionTypeBinding
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

/**
 * @file: This overlay is used to select type of box condition(condition general, device state, etc...)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxConditionTypeSelected: triggered when type of box condition is selected
 * @param onClose: triggered when hide the overlay
 */
class OverlaySelectBoxConditionType(
    context: Context,
    container: ViewGroup,
    private val onBoxConditionTypeSelected: (Int) -> Unit,
    private val onClose: () -> Unit
): OverlayBase<LayoutOverlaySelectBoxConditionTypeBinding>(
    context,
    container,
    LayoutOverlaySelectBoxConditionTypeBinding::inflate
) {
    // adapter for select type of box condition
    private val adapterBoxActionType: AdapterBoxActionType by lazy {
        AdapterBoxActionType(
            onItemClicked = {
                onBoxConditionTypeSelected.invoke(it)
            }
        )
    }
    override fun onViewCreated(binding: LayoutOverlaySelectBoxConditionTypeBinding) {
        binding.apply {
            rvBoxType.adapter = adapterBoxActionType
            adapterBoxActionType.submitList(
                listOf<Int>(
                    FTypeAction.ACT_CONDITION_GENERAL,
                    FTypeAction.ACT_CONDITION_DEVICE
                )
            )

            btnBack.setOnClickListener { onClose.invoke() }

            btnCancel.setOnClickListener {
                onClose.invoke()
            }
        }
    }
}