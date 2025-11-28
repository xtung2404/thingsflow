package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTGroup
import javax.inject.Inject

class RepoGroup @Inject constructor() {
    fun getAll(): List<IoTGroup> = SmartSdk.groupHandler().all.toList()

    fun create(
        label: String,
        type: String,
        callback: RequestResultCallback<IoTGroup>
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
        callback: RequestResultCallback<IoTGroup>
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
        callback: RequestResultCallback<Boolean>
    ) {
        FlowSdk.groupHandler().delete(
            uuid,
            callback
        )
    }

}