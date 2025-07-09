package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.AuthenticationRepository
import com.example.thingsflow.module.repository.LocationRepository
import com.example.thingsflow.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.rogocore.sdk.entity.IoTLocation
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(val repo: LocationRepository): ViewModel() {
    fun getLocationList(): MutableLiveData<MutableList<IoTLocation>> {
        val result = MutableLiveData<MutableList<IoTLocation>>()
        viewModelScope.launch {
            repo.getLocationList {
                result.postValue(it)
            }
        }
        return result
    }

    fun createLocation(
        label: String,
        type: String
    ): MutableLiveData<UIState<IoTLocation>> {
        val result = MutableLiveData<UIState<IoTLocation>>()
        viewModelScope.launch {
            repo.createLocation(
                label,
                type
            ) {
                result.postValue(it)
            }
        }
        return result
    }
}