package com.example.thingsflow.ui

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSplashBinding
import com.example.thingsflow.module.viewmodel.AuthenticationViewModel
import com.example.thingsflow.module.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.SmartSdkConnectCallback

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_splash

    private val authenticationViewModel by viewModels<AuthenticationViewModel>()
    private val locationViewModel by viewModels<LocationViewModel>()

    /**
     * check if fundamental requirements of phone, if it supports the background service
     * @param isAuthenticated indicated, if user used to sign in or not
     * getDefaultLocation(): check if a location is setted as default or not
     */
    override fun initAction() {
        super.initAction()
        authenticationViewModel
            .connectService(result = { isAuthenticated ->
                if (isAuthenticated) {
                    if (locationViewModel.getDefaultLocation() != null) {
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