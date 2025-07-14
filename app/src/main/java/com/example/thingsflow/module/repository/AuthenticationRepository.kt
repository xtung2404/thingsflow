package com.example.thingsflow.module.repository

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
        callback: AuthRequestCallback
    ) {
        val isEmail = input.contains("@")
        val username = if (isEmail) null else input
        val emailAdr = if (isEmail) input else null
        SmartSdk.signIn(
            username,
            emailAdr,
            null,
            pwd,
            callback
        )
    }

    fun signUp(
        username: String,
        email: String,
        pwd: String,
        callback: AuthRequestCallback
    ) {
        SmartSdk.signUp(
            username,
            email,
            null,
            pwd,
            callback
        )
    }

    fun signOut(
        callback: AuthRequestCallback
    ) {
        SmartSdk.signOut(callback)
    }

    fun forgotPwd(
        email: String?,
        callback: AuthRequestCallback
    ) {
        SmartSdk.forgot(
            email,
            callback
        )
    }

    fun handleOtpVerification(
        otp: String,
        pwd: String?,
        callback: AuthRequestCallback
    ) {
        SmartSdk.updatePasswordOrVerifyAccount(
            otp,
            pwd,
            callback
        )
    }
}