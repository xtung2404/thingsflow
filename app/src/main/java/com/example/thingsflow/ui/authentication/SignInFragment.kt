package com.example.thingsflow.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSignInBinding
import com.example.thingsflow.module.viewmodel.AuthenticationViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.ui.dialog.showDialogLoadingWithAnimation
import com.example.thingsflow.utils.UIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_sign_in

    private val authenticationViewModel by viewModels<AuthenticationViewModel>()
    override fun initAction() {
        super.initAction()
        binding.apply {
            btnSignIn.setOnClickListener {
                signIn()
            }

            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.signUpFragment)
            }

            txtForgotPwd.setOnClickListener {
                val email = edtEmail.text.toString()
                val bdlEmail = bundleOf("email" to email)
                findNavController().navigate(R.id.resetPasswordFragment, bdlEmail)
            }
        }
    }

    /**
     * check input of user's information
     * @param email: email of user
     * @param pwd: password of user
     */
    private fun isUserInfoValid(
        email: String,
        pwd: String
    ): Boolean {
        return email.isNotEmpty() && pwd.length >= 6
    }

    /**
     * log in with email and password
     */
    private fun signIn() {
        binding.apply {
            val dialog = context?.showDialogLoadingWithAnimation(
                R.string.signing_in,
                messStr = "hi",
                lifecycle = lifecycle
            )
            val input = edtEmail.text.toString()
            val pwd = edtPwd.text.toString()
            if (isUserInfoValid(input, pwd)) {
                authenticationViewModel.signIn(
                    input,
                    pwd
                ).observe(requireActivity()) {
                    when(it) {
                        is UIState.Success -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                dialog?.cancel()
                                findNavController().navigate(R.id.locationManagementFragment)
                            }
                        }
                        is UIState.Failure -> {

                        }
                    }
                }
            }
        }
    }
}