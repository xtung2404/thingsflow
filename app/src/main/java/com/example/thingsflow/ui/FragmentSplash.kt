package com.example.thingsflow.ui

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSplashBinding
import com.example.thingsflow.module.viewmodel.VMAuthentication
import com.example.thingsflow.module.viewmodel.VMLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSplash : BaseFragment<FragmentSplashBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_splash

    private val vmAuthentication by viewModels<VMAuthentication>()
    private val vmLocation by viewModels<VMLocation>()

    /**
     * check if fundamental requirements of phone, if it supports the background service
     * @param isAuthenticated indicated, if user used to sign in or not
     * getDefaultLocation(): check if a location is setted as default or not
     */
    override fun initAction() {
        super.initAction()
        vmAuthentication
            .connectService(result = { isAuthenticated ->
                if (isAuthenticated) {
                    if (vmLocation.getDefaultLocation() != null) {
                        findNavController().navigate(R.id.homeFragment)
                    } else {
                        findNavController().navigate(R.id.locationManagementFragment)
                    }
                } else {
                    findNavController().navigate(R.id.signInFragment)
                }
            })
    }
}