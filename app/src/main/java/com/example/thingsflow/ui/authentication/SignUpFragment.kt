package com.example.thingsflow.ui.authentication

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentSignUpBinding
import com.example.thingsflow.module.viewmodel.AuthenticationViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_sign_up

    private val authenticationViewModel by viewModels<AuthenticationViewModel>()

    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(requireContext(), findNavController().previousBackStackEntry?.destination?.id)
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener {
                signUp()
            }
        }
    }

    /**
     * sign up with email and password
     */

    private fun signUp() {
        binding.apply {
            val username = edtUsername.text.toString()
            val email = edtEmail.text.toString()
            val pwd = edtPwd.text.toString()
            if (isUserInfoValid(username, email, pwd)) {
                authenticationViewModel.signUp(
                    username,
                    email,
                    pwd,
                    object : AuthRequestCallback {
                        override fun onSuccess() {
                            CoroutineScope(Dispatchers.Main).launch {
                                findNavController().navigate(R.id.verifyOTPFragment)
                            }
                        }

                        override fun onFailure(p0: Int, p1: String?) {

                        }
                    }
                )
            }
        }
    }

    /**
     * check valid of user's information
     * @param email: email of user
     * @param password: password of user
     * @param confirmPwd: recheck the password
     */
    private fun isUserInfoValid(
        username: String,
        email: String,
        pwd: String
    ): Boolean {
        return username.isNotEmpty() && email.isNotEmpty() && pwd.length >= 6
    }
}