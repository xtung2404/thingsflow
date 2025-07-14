package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(val repo: AuthenticationRepository) :
    ViewModel() {
    fun connectService(): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            repo.connectService {
                result.postValue(it)
            }
        }
        return result
    }

    fun signIn(
        input: String,
        pwd: String,
        callback: AuthRequestCallback
    ) {
        viewModelScope.launch {
            repo.signIn(
                input,
                pwd,
                callback
            )
        }
    }

    fun signUp(
        username: String,
        email: String,
        pwd: String,
        callback: AuthRequestCallback
    ) {
        viewModelScope.launch {
            repo.signUp(
                username,
                email,
                pwd,
                callback
            )
        }
    }

    fun signOut(
        callback: AuthRequestCallback
    ) {
        viewModelScope.launch {
            repo.signOut(callback)
        }
    }

    fun forgotPwd(
        email: String?,
        callback: AuthRequestCallback
    ) {
        viewModelScope.launch {
            repo.forgotPwd(
                email,
                callback
            )
        }
    }

    fun handleOtpVerification(
        otp: String,
        pwd: String?,
        callback: AuthRequestCallback
    ) {
        viewModelScope.launch {
            repo.handleOtpVerification(
                otp,
                pwd,
                callback
            )
        }
    }
}