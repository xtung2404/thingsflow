package com.example.thingsflow.ui.location

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentLocationManagementBinding
import com.example.thingsflow.module.viewmodel.VMAuthentication
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterLocation
import com.example.thingsflow.ui.dialog.DialogDeleteLocation
import com.example.thingsflow.ui.dialog.DialogEditLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback

@AndroidEntryPoint
class FragmentLocationManagement : FragmentBase<FragmentLocationManagementBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_location_management

    private val vmLocation by activityViewModels<VMLocation>()
    private val vmAuthentication by viewModels<VMAuthentication>()
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

    private val locationAdapter: AdapterLocation by lazy {
        AdapterLocation(
            onMenuClick = {
                dialogEditLocation.show(it)
            }
        )
    }

    override fun initVariable() {
        super.initVariable()
        binding.apply {
            vmLocation.refresh()
            rvLocation.adapter = locationAdapter
            vmLocation.locationsLiveData.observe(requireActivity()) {
                locationAdapter.submitList(it)
                if (vmLocation.getDefaultLocation() != null) {
                    locationAdapter.setSelectedLocation(vmLocation.getDefaultLocation()!!)
                }
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

            btnContinue.setOnClickListener {
                val selectedLocation = locationAdapter.getSelectedLocation()
                selectedLocation?.let {
                    vmLocation.setDefaultLocation(it.uuid)
                    findNavController().navigate(R.id.homeFragment)
                }
            }

            btnSingOut.setOnClickListener {
                vmAuthentication
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