package com.example.thingsflow.ui.flowScene

import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioInfoBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.dialog.DialogFlowNodeList

class FragmentFlowScenarioInfo : FragmentBase<FragmentFlowScenarioInfoBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario_info

    private val dialogFlowNodeList: DialogFlowNodeList by lazy {
        DialogFlowNodeList(
            requireContext(),
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
//                dialogFlowNodeList.show()
                findNavController().navigate(R.id.fragmentFlowBinding)
            }
        }
    }
}