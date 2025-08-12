package com.example.thingsflow.ui.location

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentCreateLocationBinding
import com.example.thingsflow.module.viewmodel.VMAuthentication
import com.example.thingsflow.module.viewmodel.VMLocation
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.adapter.AdapterSpinnerLocationType
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation

@AndroidEntryPoint
class FragmentCreateLocation : BaseFragment<FragmentCreateLocationBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_create_location

    private val vmLocation by viewModels<VMLocation>()
    private val vmAuthentication by viewModels<VMAuthentication>()
    private lateinit var locationTypeSpinnerAdapter: AdapterSpinnerLocationType
    private var isCreatingFirstLocation: Boolean = false
    override fun initVariable() {
        super.initVariable()
        binding.apply {
            isCreatingFirstLocation = findNavController().previousBackStackEntry?.destination?.id == R.id.signUpFragment
            locationTypeSpinnerAdapter = AdapterSpinnerLocationType(
                requireContext(),
                resources.getStringArray(R.array.location_type).toList()
            )
        }
    }

    override fun initView() {
        super.initView()
        setView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(requireContext(), findNavController().previousBackStackEntry?.destination?.id)
            spinnerLocationType.adapter = locationTypeSpinnerAdapter
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener {
                val label = edtLabel.text.toString()
                createLocation(label, "")
            }

            btnLogOut.setOnClickListener {
                signOut()
            }
        }
    }

    /**
     *
     */
    private fun setView() {
        binding.apply {
            val visibility = if (isCreatingFirstLocation) View.INVISIBLE else View.VISIBLE
            toolbar.btnBack.visibility = visibility
            toolbar.txtTitle.visibility = visibility
            btnLogOut.visibility = if (isCreatingFirstLocation) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun createLocation(
        label: String,
        type: String
    ) {
        vmLocation.createLocation(
            label,
            type,
            object : RequestCallback<IoTLocation> {
                override fun onSuccess(p0: IoTLocation?) {
                    CoroutineScope(Dispatchers.Main).launch {
                        findNavController().navigate(R.id.locationManagementFragment)
                    }
                }

                override fun onFailure(p0: Int, p1: String?) {

                }

            }
        )
    }

    private fun signOut() {
        vmAuthentication
            .signOut(
                object : AuthRequestCallback {
                    override fun onSuccess() {
                        findNavController().navigate(R.id.signInFragment)
                    }

                    override fun onFailure(p0: Int, p1: String?) {

                    }

                }
            )
    }
}