package com.example.thingsflow.ui.flow

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentFlowManagementBinding
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterFlowScenario
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.flowcommon.FlowSceneTemplate
import java.util.concurrent.Flow

@AndroidEntryPoint
class FragmentFlowScenManagement : FragmentBase<FragmentFlowManagementBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_flow_management
    private val flowSceneList: ArrayList<FlowSceneTemplate> = arrayListOf()
    private val adapterFlowScenario: AdapterFlowScenario by lazy {
        AdapterFlowScenario(
            onItemClick = {
                findNavController().navigate(R.id.fragmentFlowScenarioInfo)
            }
        )
    }

    override fun initView() {
        super.initView()
        binding.apply {
            rvFlowScenario.adapter = adapterFlowScenario
            val flowScene1 = FlowSceneTemplate()
            flowScene1.apply {
                id = "1"
                name = "hihihihi"
            }
            flowSceneList.add(flowScene1)
            val flowScene2 = FlowSceneTemplate()
            flowScene2.apply {
                id = "2"
                name = "hih2ihihi"
            }
            flowSceneList.add(flowScene2)
            val flowScene3 = FlowSceneTemplate()
            flowScene3.apply {
                id = "3"
                name = "333333333hihihihi"
            }
            flowSceneList.add(flowScene3)

            val flowScene4 = FlowSceneTemplate()
            flowScene4.apply {
                id = "4"
                name = "h43545reytfgeihihihi"
            }
            flowSceneList.add(flowScene4)
            val flowScene5 = FlowSceneTemplate()
            flowScene5.apply {
                id = "45354"
                name = "hih435345ergfdgihihi"
            }
            flowSceneList.add(flowScene5)
            if (flowSceneList.isEmpty()) {
                lnEmpty.visibility = View.VISIBLE
                flAvailable.visibility = View.GONE
            } else {
                lnEmpty.visibility = View.GONE
                flAvailable.visibility = View.VISIBLE
            }
            adapterFlowScenario.submitList(flowSceneList)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            btnCreate.setOnClickListener {
                findNavController().navigate(R.id.fragmentFlowScenario)
            }

            btnAdd.setOnClickListener {
                findNavController().navigate(R.id.fragmentCreateFlowScenario)
            }


        }
    }
}