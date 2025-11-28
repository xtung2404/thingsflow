package com.example.thingsflow.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thingsflow.R
import com.example.thingsflow.databinding.LayoutItemConnectivityBinding
import rogo.iot.module.base.entity.IoTNetworkConnectivity

class AdapterConnectivity(
    private val onItemClick: (Int) -> Unit
): ListAdapter<Map.Entry<IoTNetworkConnectivity, Boolean>, AdapterConnectivity.ConnectivityViewHolder>(
    object : DiffUtil.ItemCallback<Map.Entry<IoTNetworkConnectivity, Boolean>>() {
        override fun areItemsTheSame(
            oldItem: Map.Entry<IoTNetworkConnectivity, Boolean>,
            newItem: Map.Entry<IoTNetworkConnectivity, Boolean>
        ): Boolean {
            return oldItem.key.infType == newItem.key.infType && oldItem.value == newItem.value
        }

        override fun areContentsTheSame(
            oldItem: Map.Entry<IoTNetworkConnectivity, Boolean>,
            newItem: Map.Entry<IoTNetworkConnectivity, Boolean>
        ): Boolean {
            return oldItem.key.infType == newItem.key.infType && oldItem.value == newItem.value
        }
    }
) {
    inner class ConnectivityViewHolder(
        private val binding: LayoutItemConnectivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(connectivity: Map.Entry<IoTNetworkConnectivity, Boolean>) {
            binding.apply {
                when (connectivity.key.infType) {
                    IoTNetworkConnectivity.WIFI -> {
                        txtLabel.text = root.context.getString(R.string.wifi)
                    }

                    IoTNetworkConnectivity.ETHERNET -> {
                        txtLabel.text = root.context.getString(R.string.cable_connectivity)
                    }

                    else -> {
                    }
                }
                if (connectivity.value) {
                    txtDesc.text = root.context.getString(R.string.connected)
                } else {
                    txtDesc.text = root.context.getString(R.string.no_available_connection)
                }

                root.setOnClickListener {
                    onItemClick.invoke(connectivity.key.infType)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectivityViewHolder {
        val inflater = LayoutItemConnectivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConnectivityViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ConnectivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
