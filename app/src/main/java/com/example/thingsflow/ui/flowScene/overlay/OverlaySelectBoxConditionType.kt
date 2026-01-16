package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.thingsflow.databinding.LayoutOverlaySelectBoxConditionTypeBinding
import com.example.thingsflow.module.viewmodel.VMFlowScene
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterBoxActionType
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp
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
    private val vmFlowScene: VMFlowScene? by lazy {
        viewModelOwner?.let {
            ViewModelProvider(it)[VMFlowScene::class.java]
        }
    }
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

            btnBack.setOnClickListener { onClose.invoke() }

            btnCancel.setOnClickListener {
                onClose.invoke()
            }
        }
    }

    override fun show() {
        super.show()
        binding.apply {
            val list = mutableListOf<Int>()
            when(getPreviousBox()) {
                is FBoxActionCallHttp -> {
                    list.add(FBoxType.ACT_CONDITION_GENERAL)
                }
                else -> {
                    list.add(FBoxType.ACT_CONDITION_DEVICE)
                }
            }
            adapterBoxActionType.submitList(list)
        }
    }

    private fun getPreviousBox(): FBox? {
        val previousBoxId = vmFlowScene?.getRootBoxId()
        return vmFlowScene?.boxes?.value?.find { it.id == previousBoxId }
    }
}