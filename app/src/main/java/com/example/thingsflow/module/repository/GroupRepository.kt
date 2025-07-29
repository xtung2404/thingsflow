package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTGroup
import rogo.iot.module.rogocore.sdk.entity.IoTLocation
import javax.inject.Inject

class GroupRepository @Inject constructor() {
    fun getAll(): List<IoTGroup> = SmartSdk.groupHandler().all.toList()

    fun create(
        label: String,
        type: String,
        callback: RequestCallback<IoTGroup>
    ) {
        FlowSdk.groupHandler().createGroup(
            label,
            type,
            callback
        )
    }

    fun update(
        ioTGroup: IoTGroup,
        label: String,
        callback: RequestCallback<IoTGroup>
    ) {
        FlowSdk.groupHandler().updateGroup(
            ioTGroup.uuid,
            label,
            ioTGroup.desc,
            callback
        )
    }

    fun delete(
        uuid: String,
        callback: RequestCallback<Boolean>
    ) {
        FlowSdk.groupHandler().delete(
            uuid,
            callback
        )
    }

}