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
import com.example.thingsflow.databinding.FragmentVerifyResetPwdOtpBinding
import com.example.thingsflow.module.viewmodel.AuthenticationViewModel
import com.example.thingsflow.ui.BaseFragment
import com.example.thingsflow.utils.UIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rogo.iot.module.platform.ILogR

@AndroidEntryPoint
class VerifyResetPwdOtpFragment : BaseFragment<FragmentVerifyResetPwdOtpBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_verify_reset_pwd_otp
    private val authenticationViewModel by viewModels<AuthenticationViewModel>()
    private val TAG = "VerifyResetPwdOtpFragment"
    private var countDownTiming = 60
    private var countdownJob: Job?= null

    override fun initVariable() {
        super.initVariable()
        binding.apply {
            txtSendingOtp.text = resources.getString(R.string.sending_otp)
            txtTimeOut.text = "${countDownTiming} s"
            startCountdownTimer(
                onTick = {
                    txtTimeOut.text = "${it} s"
                },
                onFinish = {
                    txtSendingOtp.text = resources.getString(R.string.otp_code_is_false )
                    txtTimeOut.text = resources.getString(R.string.resend)
                }
            )
        }

    }

    override fun initAction() {
        super.initAction()
        binding.apply {
            val edtOtps = listOf(
                edtOtp1,
                edtOtp2,
                edtOtp3,
                edtOtp4,
                edtOtp5,
                edtOtp6
            )
            for (i in edtOtps.indices) {
                edtOtps[i].addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s?.length == 1 && i < edtOtps.size - 1) {
                            edtOtps[i + 1].requestFocus()
                        }
                        val isAllFilled = edtOtps.all { it.text.length == 1 }
                        if (isAllFilled) {
                            ILogR.D(TAG, "otpCode:", edtOtps.joinToString("") { it.text.toString() })
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            }


            btnConfirm.setOnClickListener {
                val otpCode = edtOtps.joinToString("") { it.text.toString() }
                val newPwd = edtPwd.text.toString()
                handleOtpVerification(otpCode, newPwd)
            }
        }
    }
    /**
     * verify otp code
     * @param otpCode: code user got from mail
     * @param newPwd: new password of user
     */
    private fun handleOtpVerification(
        otpCode: String,
        newPwd: String
    ) {
        authenticationViewModel.handleOtpVerification(
            otpCode,
            newPwd
        ).observe(requireActivity()) {
            when(it) {
                is UIState.Success -> {
                    countdownJob?.cancel()
                    findNavController().navigate(R.id.locationManagementFragment)
                }

                is UIState.Failure -> {

                }
            }
        }
    }

    /**
     * count down timer
     */
    private fun startCountdownTimer(
        onTick:(Int) -> Unit,
        onFinish: () -> Unit
    ) {
        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            for (i in countDownTiming downTo 1) {
                onTick(i)
                delay(1000)
            }
            onFinish()
        }
    }

}