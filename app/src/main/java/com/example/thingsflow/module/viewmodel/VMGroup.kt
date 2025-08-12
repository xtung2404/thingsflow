package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thingsflow.module.repository.RepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.entity.IoTGroup
import javax.inject.Inject

@HiltViewModel
class VMGroup @Inject constructor(val repo: RepoGroup): ViewModel() {
    private val _groupsLiveData: MutableLiveData<List<IoTGroup>> = MutableLiveData()
    val groupsLiveData: LiveData<List<IoTGroup>> = _groupsLiveData


    fun getAll(): List<IoTGroup> {
        val groups = repo.getAll()
        _groupsLiveData.value = groups
        return groups
    }
    fun refresh() {
        _groupsLiveData.value = repo.getAll()
    }
    fun create(
        label: String,
        type: String,
        callback: RequestCallback<IoTGroup>
    ) {
        viewModelScope.launch {
            repo.create(
                label,
                type,
                callback
            )
        }

    }

    fun update(
        ioTGroup: IoTGroup,
        label: String,
        callback: RequestCallback<IoTGroup>
    ) {
        viewModelScope.launch {
            repo.update(
                ioTGroup,
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
            repo.delete(
                uuid,
                callback
            )
        }
    }
}