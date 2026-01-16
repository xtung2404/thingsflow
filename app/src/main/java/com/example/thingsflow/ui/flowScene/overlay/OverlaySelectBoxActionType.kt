package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxActionTypeBinding
import com.example.thingsflow.module.viewmodel.VMFlowScene
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxActionType
import rogo.iot.module.flowcommon.type.FBoxType

/**
 * @file: This overlay is used to select type of action box(call http, control device)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxActionTypeSelected: triggered when type of box action is selected
 * @param onClose: triggered when hide the overlay
 */
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
    private val vmFlowScene: VMFlowScene? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScene::class.java]
        }
    }

    // adapter for select type of box action
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
                    FBoxType.ACT_CONTROL_DEVICE,
                    FBoxType.ACT_CALL_HTTP
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

    override fun show() {
        super.show()
        binding.apply {

        }
    }
}