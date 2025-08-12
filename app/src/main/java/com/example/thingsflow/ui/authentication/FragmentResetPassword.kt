package com.example.thingsflow.ui.authentication

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.thingsflow.R
import com.example.thingsflow.databinding.FragmentResetPasswordBinding
import com.example.thingsflow.module.viewmodel.VMAuthentication
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.utils.getFragmentLabel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback

@AndroidEntryPoint
class FragmentResetPassword : BaseFragment<FragmentResetPasswordBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_reset_password

    private var email: String?= ""
    private val vmAuthentication by viewModels<VMAuthentication>()

    override fun initVariable() {
        super.initVariable()
        arguments?.let {
            email = it.getString("email")
        }
    }
    override fun initView() {
        super.initView()
        binding.apply {
            toolbar.txtTitle.text = getFragmentLabel(requireContext(), findNavController().previousBackStackEntry?.destination?.id)
            toolbar.btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
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
                vmAuthentication.forgotPwd(
                    email,
                    object : AuthRequestCallback {
                        override fun onSuccess() {
                            CoroutineScope(Dispatchers.Main).launch {
                                findNavController().navigate(R.id.verifyResetPwdOtpFragment)
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