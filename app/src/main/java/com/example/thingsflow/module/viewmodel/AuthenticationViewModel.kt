package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.AuthenticationRepository
import com.example.thingsflow.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(val repo: AuthenticationRepository): ViewModel() {
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
        pwd: String
    ): MutableLiveData<UIState<String>> {
        val result = MutableLiveData<UIState<String>>()
        viewModelScope.launch {
            repo.signIn(
                input,
                pwd
            ) {
                result.postValue(it)
            }
        }
        return result
    }

    fun signUp(
        username: String,
        email: String,
        pwd: String
    ): MutableLiveData<UIState<String>> {
        val result = MutableLiveData<UIState<String>>()
        viewModelScope.launch {
            repo.signUp(
                username,
                email,
                pwd
            ) {
                result.postValue(it)
            }
        }
        return result
    }

    fun signOut(): MutableLiveData<UIState<String>> {
        val result = MutableLiveData<UIState<String>>()
        viewModelScope.launch {
            repo.signOut {
                result.postValue(it)
            }
        }
        return result
    }

    fun forgotPwd(
        email: String?
    ): MutableLiveData<UIState<String>> {
        val result = MutableLiveData<UIState<String>>()
        viewModelScope.launch {
            repo.forgotPwd(email) {
                result.postValue(it)
            }
        }
        return result
    }

    fun handleOtpVerification(
        otp: String,
        pwd: String?
    ): MutableLiveData<UIState<String>> {
        val result = MutableLiveData<UIState<String>>()
        viewModelScope.launch {
            repo.handleOtpVerification(
                otp,
                pwd
            ) {
                result.postValue(it)
            }
        }
        return result
    }
}