package com.example.thingsflow.module.model

import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

data class ConfigZigbeeDeviceModel(
    val device: IoTPairedZigbeeDevice?,
    var label: String?= null,
    var groupId: String?= null
)
