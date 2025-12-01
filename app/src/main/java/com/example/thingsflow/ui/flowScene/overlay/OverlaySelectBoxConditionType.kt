package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxConditionTypeBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxActionType
import rogo.iot.module.flowcommon.type.FBoxType

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
                    FBoxType.ACT_CONDITION_GENERAL,
                    FBoxType.ACT_CONDITION_DEVICE
                )
            )

            btnBack.setOnClickListener { onClose.invoke() }

            btnCancel.setOnClickListener {
                onClose.invoke()
            }
        }
    }
}