package com.example.thingsflow.module.viewmodel

import androidx.lifecycle.ViewModel
import com.example.thingsflow.module.repository.RepoDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import javax.inject.Inject

@HiltViewModel
class VMDevice
@Inject constructor(val repo: RepoDevice) :ViewModel()
{
    private val TAG = "VMDevice"

    fun getAll(): List<IoTDevice?> = repo.getAll()

    fun getUserDevices(): List<IoTDevice> = repo.getUserDevices()

}