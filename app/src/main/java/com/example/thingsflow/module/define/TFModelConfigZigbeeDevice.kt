package com.example.thingsflow.module.define

import rogo.iot.module.rogocore.sdk.entity.IoTPairedZigbeeDevice

data class TFModelConfigZigbeeDevice(
    val device: IoTPairedZigbeeDevice?,
    var label: String?= null,
    var groupId: String?= null
)
