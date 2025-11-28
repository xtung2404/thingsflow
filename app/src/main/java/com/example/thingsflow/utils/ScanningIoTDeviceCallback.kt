package com.example.thingsflow.utils

import rogo.iot.module.base.entity.IoTModelSmartConfig

interface ScanningIoTDeviceCallback {
    fun onDeviceFound(device: IoTModelSmartConfig)
    fun onTimeOut()
}