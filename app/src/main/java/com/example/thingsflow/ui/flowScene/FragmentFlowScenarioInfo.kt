package com.example.thingsflow.ui.flowScene

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowScenarioInfoBinding
import com.example.thingsflow.module.viewmodel.VMFlowBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.dialog.DialogFlowNodeList
import kotlinx.coroutines.flow.flow

/**
* @file is to show information of a flow scenario, that includes: flow scenario is launched on which devices
*/
class FragmentFlowScenarioInfo : FragmentBase<FragmentFlowScenarioInfoBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_flow_scenario_info

    private val vmFlowBinding: VMFlowBinding by activityViewModels<VMFlowBinding>()

    //flowSceneId is to store uuid of the current flow scenario
    private var flowSceneId: String?= null

    //dialogFlowNodeList is used to show list of gateway
    private val dialogFlowNodeList: DialogFlowNodeList by lazy {
        DialogFlowNodeList(
            requireContext(),
            onConnectNewNode = {
                findNavController().navigate(R.id.identifyDeviceFragment)
            },
            onDevicesSelected = {
                vmFlowBinding.setSelectedGateways(it)
                findNavController().navigate(R.id.fragmentFlowBinding)
            }
        )
    }
    override fun initVariable() {
        super.initVariable()
        arguments?.let {
            flowSceneId = arguments?.getString("flowSceneId")
        }
        flowSceneId?.let {
            vmFlowBinding.setSelectedFlowScene(it)
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = context?.getString(R.string.list_of_flow_scenario)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentFlowScenManagement)
            }

            btnExecuteFlow.setOnClickListener {
                dialogFlowNodeList.show()
            }
        }
    }
}