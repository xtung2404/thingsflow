package com.example.thingsflow.ui.location

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentLocationManagementBinding
import com.example.thingsflow.module.viewmodel.AuthenticationViewModel
import com.example.thingsflow.module.viewmodel.LocationViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.LocationAdapter
import com.example.thingsflow.ui.dialog.DialogDeleteLocation
import com.example.thingsflow.ui.dialog.DialogEditLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback

@AndroidEntryPoint
class LocationManagementFragment : BaseFragment<FragmentLocationManagementBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_location_management

    private val locationViewModel by activityViewModels<LocationViewModel>()
    private val authenticationViewModel by viewModels<AuthenticationViewModel>()
    private val dialogEditLocation: DialogEditLocation by lazy {
        DialogEditLocation(
            requireContext(),
            requireActivity(),
            onDeleteLoc = {
                dialogEditLocation.dismiss()
                dialogDeleteLocation.show(it)
            }
        )
    }

    private val dialogDeleteLocation: DialogDeleteLocation by lazy {
        DialogDeleteLocation(
            requireContext(),
            requireActivity(),
            onCancel = {
                dialogDeleteLocation.dismiss()
                dialogEditLocation.show(it)
            }
        )
    }
    private val locationAdapter: LocationAdapter by lazy {
        LocationAdapter(
            onMenuClick = {
                dialogEditLocation.show(it)
            }
        )
    }

    override fun initVariable() {
        super.initVariable()
        binding.apply {
            locationViewModel.refresh()
            rvLocation.adapter = locationAdapter
            locationViewModel.locationsLiveData.observe(requireActivity()) {
                locationAdapter.submitList(it)
            }
        }
    }

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = "Man hinh chinh"
        }
    }


    override fun initAction() {
        super.initAction()
        binding.apply {
            btnCreate.setOnClickListener {
                findNavController().navigate(R.id.createLocationFragment)
            }

            btnSingOut.setOnClickListener {
                authenticationViewModel
                    .signOut(
                        object : AuthRequestCallback {
                            override fun onSuccess() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    findNavController().navigate(R.id.signInFragment)
                                }
                            }

                            override fun onFailure(p0: Int, p1: String?) {

                            }

                        }
                    )
            }
        }
    }
}