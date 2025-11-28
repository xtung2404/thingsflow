package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import rogo.iot.module.rogocore.sdk.entity.IoTDevice
import javax.inject.Inject

class RepoDevice @Inject constructor() {
    private val TAG = "RepoDevice"
    val handler = FlowSdk.deviceHandler()

    fun getAll(): List<IoTDevice?> = handler.all.toList()

    fun getUserDevices(): List<IoTDevice> = handler.userDevices.toList()


}