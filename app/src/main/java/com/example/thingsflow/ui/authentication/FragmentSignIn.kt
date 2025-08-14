package com.example.thingsflow.ui.authentication

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSignInBinding
import com.example.thingsflow.module.viewmodel.VMAuthentication
import com.example.thingsflow.ui.FragmentBase
import com.example.thingsflow.ui.dialog.showDialogLoadingWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback

@AndroidEntryPoint
class FragmentSignIn : FragmentBase<FragmentSignInBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_sign_in

    private val vmAuthentication by viewModels<VMAuthentication>()
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
     * handle user's sign-in process.
     * Required inputs: email or username, and password
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
                vmAuthentication.signIn(
                    input,
                    pwd,
                    object : AuthRequestCallback {
                        override fun onSuccess() {
                            CoroutineScope(Dispatchers.Main).launch {
                                dialog?.cancel()
                                findNavController().navigate(R.id.locationManagementFragment)
                            }
                        }

                        override fun onFailure(p0: Int, p1: String?) {
                            CoroutineScope(Dispatchers.Main).launch {
                                dialog?.cancel()
                            }
                        }
                    }
                )
            }
        }
    }
}