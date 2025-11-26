package com.example.thingsflow.ui.deviceConfig.gateway

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentLocationManagementBinding
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.adapter.AdapterLocation
import com.example.thingsflow.ui.dialog.DialogDeleteLocation
import com.example.thingsflow.ui.dialog.DialogEditLocation
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.platform.ILogR

@AndroidEntryPoint
class FragmentLocationManagement : FragmentBase<FragmentLocationManagementBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_location_management
    private val TAG = "FragmentLocationManagement"

    private val vmLocation by activityViewModels<VMLocation>()
    private val dialogEditLocation: DialogEditLocation by lazy {
        DialogEditLocation(
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
            toolbar.txtTitle.text = resources.getString(R.string.list_of_device)
        }
    }


    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().navigate(R.id.fragmentDevice)
            }
            btnCreate.setOnClickListener {
                findNavController().navigate(R.id.createLocationFragment)
            }

            btnContinue.setOnClickListener {
                val selectedLocation = locationAdapter.getSelectedLocation()
                selectedLocation?.let {
                    vmLocation.setDefaultLocation(it.uuid)
                    ILogR.D(TAG, "initAction:getDefaultLocation", vmLocation.getDefaultLocation())
                    findNavController().navigate(R.id.identifyDeviceFragment)
                }
            }
        }
    }
}