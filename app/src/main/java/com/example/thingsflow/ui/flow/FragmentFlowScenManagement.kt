package com.example.thingsflow.ui.flow

import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowManagementBinding
import com.example.thingsflow.ui.BaseFragment

class FragmentFlowScenManagement : BaseFragment<FragmentFlowManagementBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_flow_management

    override fun initView() {
        super.initView()
        binding.apply {

        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnCreate.setOnClickListener {
//                findNavController().navigate()
            }
        }
    }
}