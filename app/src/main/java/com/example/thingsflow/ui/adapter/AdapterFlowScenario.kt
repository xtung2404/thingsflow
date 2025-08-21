package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingflowsdk.core.FlowSdk
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemDeviceGridBinding
import com.example.thingsflow.databinding.LayoutItemDeviceSingleBinding
import com.example.thingsflow.databinding.LayoutItemElementBinding
import com.example.thingsflow.databinding.LayoutItemFlowScenarioBinding
import rogo.iot.module.flowcommon.FlowSceneTemplate
import rogo.iot.module.platform.entity.IoTElementInfo
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import java.util.concurrent.Flow

class AdapterFlowScenario(
    private val onItemClick: (FlowSceneTemplate) -> Unit
):
ListAdapter<FlowSceneTemplate, AdapterFlowScenario.FlowSceneTemplateViewHolder>(
    object : DiffUtil.ItemCallback<FlowSceneTemplate>() {
        override fun areItemsTheSame(
            oldItem: FlowSceneTemplate,
            newItem: FlowSceneTemplate
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: FlowSceneTemplate,
            newItem: FlowSceneTemplate
        ): Boolean {
            return false
        }

    }
) {
    inner class FlowSceneTemplateViewHolder(
        private val binding: LayoutItemFlowScenarioBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun onBind(flowScene: FlowSceneTemplate) {
            binding.apply {
                txtFlowLabel.text = flowScene.name
                btnMenu.setOnClickListener {

                }

                root.setOnClickListener {
                    onItemClick.invoke(flowScene)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowSceneTemplateViewHolder {
       val inflater = LayoutItemFlowScenarioBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false
       )
        return FlowSceneTemplateViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: FlowSceneTemplateViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}