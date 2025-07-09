package com.example.thingsflow.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentResetPasswordBinding
import com.example.thingsflow.module.viewmodel.AuthenticationViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.utils.UIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment<FragmentResetPasswordBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_reset_password

    private var email: String?= ""
    private val authenticationViewModel by viewModels<AuthenticationViewModel>()

    override fun initVariable() {
        super.initVariable()
        arguments?.let {
            email = it.getString("email")
        }
    }
    override fun initView() {
        super.initView()
        binding.apply {
            email?.let {
                edtEmail.setText(it)
                txtEmail.text = it
            }
        }
    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            edtEmail.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        email = s.toString()
                        txtEmail.text = email
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
            )

            btnContinue.setOnClickListener {
                authenticationViewModel.forgotPwd(
                    email
                ).observe(requireActivity()) {
                    when(it) {
                        is UIState.Success -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                findNavController().navigate(R.id.verifyResetPwdOtpFragment)
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