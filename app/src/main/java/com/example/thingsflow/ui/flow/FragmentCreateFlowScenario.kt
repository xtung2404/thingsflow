package com.example.thingsflow.ui.flow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentCreateFlowScenarioBinding
import com.example.thingsflow.ui.FragmentBase

class FragmentCreateFlowScenario : FragmentBase<FragmentCreateFlowScenarioBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_create_flow_scenario

    override fun initVariable() {
        super.initVariable()
        binding.apply {

        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            cbCreateBlank.isChecked = false
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnCreateBlank.setOnClickListener {
                cbCreateBlank.isChecked = true
            }

            btnContinue.setOnClickListener {
                findNavController().navigate(R.id.fragmentFlowScenario)
            }


        }
    }
}