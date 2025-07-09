package com.example.thingsflow.module.repository

import com.example.thingsflow.utils.UIState
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.callback.SmartSdkConnectCallback
import javax.inject.Inject

class AuthenticationRepository @Inject constructor() {
    fun connectService(
        result: (Boolean) -> Unit
    ) {
        SmartSdk.connectService(
            object : SmartSdkConnectCallback {
                override fun onConnected(isAuthenticated: Boolean) {
                    result.invoke(isAuthenticated)
                }

                override fun onDisconnected() {
                    SmartSdk.closeConnectionService()
                }
            }
        )
    }
    fun signIn(
        input: String,
        pwd: String,
        result: (UIState<String>) -> Unit
    ) {
        val isEmail = input.contains("@")
        val username = if (isEmail) null else input
        val emailAdr = if (isEmail) input else null
        SmartSdk.signIn(
            username,
            emailAdr,
            null,
            pwd,
            object : AuthRequestCallback {
                override fun onSuccess() {
                    result.invoke(UIState.Success(null))
                }

                override fun onFailure(p0: Int, p1: String?) {
                    result.invoke(UIState.Failure(p0, p1))
                }
            }
        )
    }

    fun signUp(
        username: String,
        email: String,
        pwd: String,
        result: (UIState<String>) -> Unit
    ) {
        SmartSdk.signUp(
            username,
            email,
            null,
            pwd,
            object : AuthRequestCallback {
                override fun onSuccess() {
                    result.invoke(UIState.Success(null))
                }

                override fun onFailure(p0: Int, p1: String?) {
                    result.invoke(UIState.Failure(p0, p1))
                }
            }
        )
    }

    fun signOut(
        result: (UIState<String>) -> Unit
    ) {
        SmartSdk.signOut(
            object : AuthRequestCallback {
                override fun onSuccess() {
                    result.invoke(UIState.Success(null))
                }

                override fun onFailure(p0: Int, p1: String?) {
                    result.invoke(UIState.Failure(p0, p1))
                }
            }
        )
    }

    fun forgotPwd(
        email: String?,
        result: (UIState<String>) -> Unit
    ) {
        SmartSdk.forgot(
            email,
            object : AuthRequestCallback {
                override fun onSuccess() {
                    result.invoke(UIState.Success(null))
                }

                override fun onFailure(p0: Int, p1: String?) {
                    result.invoke(UIState.Failure(p0, p1))
                }
            }
        )
    }

    fun handleOtpVerification(
        otp: String,
        pwd: String?,
        result: (UIState<String>) -> Unit
    ) {
        SmartSdk.updatePasswordOrVerifyAccount(
            otp,
            pwd,
            object : AuthRequestCallback {
                override fun onSuccess() {
                    result.invoke(UIState.Success(null))
                }

                override fun onFailure(p0: Int, p1: String?) {
                    result.invoke(UIState.Failure(p0, p1))
                }
            }
        )
    }
}