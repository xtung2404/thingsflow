package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.ViewGroup
import com.example.thingsflow.databinding.LayoutOverlaySetControlDeviceBinding
import com.example.thingsflow.module.define.TFElementCmd
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterConfigControlCommand
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.base.define.IoTDeviceType

/**
 * @file: This overlay is used to select type of box(action or condition)
 *
 * @param context The application/Activity context.
 * @param container The ViewGroup that hosts this overlay (usually the Root View).
 * @param onBoxTypeSelected: triggered when type of box is selected
 * @param onClose: triggered when hide the overlay
 */
class OverlaySetControlDevice(
    context: Context,
    container: ViewGroup,
    private val onCommandSetted: (devType: Int?, attrs: IntArray?, HashMap<String?, ArrayList<TFElementCmd>>) -> Unit,
    private val onClose: () -> Unit
) : OverlayBase<LayoutOverlaySetControlDeviceBinding>(
    context,
    container,
    LayoutOverlaySetControlDeviceBinding::inflate
) {
    private var action: Int = IoTAttribute.ACT_ONOFF
    private var selectedDeviceType: Int = IoTDeviceType.ALL
    private lateinit var adapterConfigControlCommand: AdapterConfigControlCommand

    // adapter for select type of box

    override fun onViewCreated(binding: LayoutOverlaySetControlDeviceBinding) {
        binding.apply {

        }
    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            btnCancel.setOnClickListener {
                onClose.invoke()
            }

            btnBack.setOnClickListener {
                onClose.invoke()
            }

            btnSave.setOnClickListener {
                onCommandSetted.invoke(
                    selectedDeviceType,
                    intArrayOf(action),
                    adapterConfigControlCommand.getDeviceActionMap()
                )
            }
        }
    }

    fun show(devType: Int?, actions: IntArray?, devMap: HashMap<String?, IntArray>) {
        super.show()
        binding.apply {
            devType?.let {
                selectedDeviceType = it
            }
            actions?.let {
                this@OverlaySetControlDevice.action = actions.first()
                adapterConfigControlCommand = AdapterConfigControlCommand(
                    context,
                    action
                )
                rvAction.adapter = adapterConfigControlCommand
                adapterConfigControlCommand.submitList(devMap.entries.toList())
            }
        }
    }


}