package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTLocation
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(val repo: LocationRepository): ViewModel() {
    private val _locationsLiveData: MutableLiveData<List<IoTLocation>> = MutableLiveData()
    val locationsLiveData: LiveData<List<IoTLocation>> = _locationsLiveData


    fun refresh() {
        _locationsLiveData.value = repo.getLocationList()
    }
    fun createLocation(
        label: String,
        type: String,
        callback: RequestCallback<IoTLocation>
    ) {
        viewModelScope.launch {
            repo.createLocation(
                label,
                type,
                callback
            )
        }

    }

    fun update(
        ioTLocation: IoTLocation,
        label: String,
        callback: RequestCallback<IoTLocation>
    ) {
        viewModelScope.launch {
            repo.editLocation(
                ioTLocation,
                label,
                callback
            )
        }
    }

    fun delete(
        uuid: String,
        callback: RequestCallback<Boolean>
    ) {
        viewModelScope.launch {
            repo.deleteLocation(
                uuid,
                callback
            )
        }
    }

    fun setDefaultLocation(uuid: String) {
        viewModelScope.launch {
            repo.setDefaultLocation(uuid)
        }
    }
    fun getDefaultLocation(): String? = repo.getDefaultLocation()
}