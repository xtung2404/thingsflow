package com.example.thingsflow.utils

import rogo.iot.module.platform.entity.IoTDirectDeviceInfo

interface ScanningIoTDeviceCallback {
    fun onDeviceFound(device: IoTDirectDeviceInfo)
    fun onTimeOut()
}