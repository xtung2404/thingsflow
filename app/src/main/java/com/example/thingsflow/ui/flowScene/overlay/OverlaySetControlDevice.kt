package com.example.thingsflow.ui.flowScene.overlay

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.databinding.LayoutItemSetControlActionElmBinding
import com.example.thingsflow.databinding.LayoutItemSetControlActionGridBinding
import com.example.thingsflow.databinding.LayoutItemSetControlActionSingleBinding
import com.example.thingsflow.databinding.LayoutOverlaySetControlDeviceBinding
import com.example.thingsflow.ui.OverlayBase
import com.example.thingsflow.ui.adapter.AdapterConfigControlCommand
import com.example.thingsflow.utils.show
import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.base.define.IoTDeviceType
import rogo.iot.module.platform.entity.IoTElementInfo

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
    private val onCommandSetted: (devType: Int?, attrs: IntArray?, HashMap<String?, IntArray>) -> Unit,
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
                    adapterConfigControlCommand.getActionMap()
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