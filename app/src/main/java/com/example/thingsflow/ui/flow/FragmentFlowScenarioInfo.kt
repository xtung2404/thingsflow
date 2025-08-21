package com.example.thingsflow.ui.flow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentCreateFlowScenarioBinding
import com.example.thingsflow.databinding.FragmentFlowScenarioInfoBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterFlowNode
import com.example.thingsflow.ui.dialog.DialogFlowNodeList
import kotlinx.coroutines.flow.flow
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice

class FragmentFlowScenarioInfo : FragmentBase<FragmentFlowScenarioInfoBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario_info

    private val dialogFlowNodeList: DialogFlowNodeList by lazy {
        DialogFlowNodeList(
            requireContext(),
            requireActivity(),
            onConnectNewNode = {
                findNavController().navigate(R.id.identifyDeviceFragment)
            }
        )
    }
    override fun initVariable() {
        super.initVariable()

    }

    override fun initView() {
        super.initView()
        binding.apply {

        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnExecuteFlow.setOnClickListener {
                dialogFlowNodeList.show()
            }
        }
    }
}