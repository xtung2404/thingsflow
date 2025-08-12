package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback
import rogo.iot.module.rogocore.sdk.callback.SmartSdkConnectCallback
import javax.inject.Inject

class RepoAuthentication @Inject constructor() {
    fun connectService(
        result: (Boolean) -> Unit
    ) {
        FlowSdk.connectService(
            object : SmartSdkConnectCallback {
                override fun onConnected(isAuthenticated: Boolean) {
                    result.invoke(isAuthenticated)
                }

                override fun onDisconnected() {
                    FlowSdk.closeServiceConnection()
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
        FlowSdk.signIn(
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
        FlowSdk.signUp(
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
        FlowSdk.signOut(callback)
    }

    fun forgotPwd(
        email: String?,
        callback: AuthRequestCallback
    ) {
        FlowSdk.forgot(
            email,
            callback
        )
    }

    fun handleOtpVerification(
        otp: String,
        pwd: String?,
        callback: AuthRequestCallback
    ) {
        FlowSdk.updatePasswordOrVerifyAccount(
            otp,
            pwd,
            callback
        )
    }
}