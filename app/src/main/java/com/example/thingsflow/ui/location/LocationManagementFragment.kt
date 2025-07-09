package com.example.thingsflow.ui.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentLocationManagementBinding
import com.example.thingsflow.module.viewmodel.LocationViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.LocationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationManagementFragment : BaseFragment<FragmentLocationManagementBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_location_management

    private val locationViewModel by viewModels<LocationViewModel>()
    private val locationAdapter: LocationAdapter by lazy {
        LocationAdapter()
    }
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            rvLocation.adapter = locationAdapter
            locationViewModel.getLocationList().observe(requireActivity()) {
                CoroutineScope(Dispatchers.Main).launch {
                    locationAdapter.submitList(it)
                }
            }
        }
    }


    override fun initAction() {
        super.initAction()
        binding.apply {
            btnCreate.setOnClickListener {
                findNavController().navigate(R.id.createLocationFragment)
            }
        }
    }
}