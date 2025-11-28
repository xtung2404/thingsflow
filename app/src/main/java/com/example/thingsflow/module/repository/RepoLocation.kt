package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import rogo.iot.module.base.callback.RequestResultCallback
import rogo.iot.module.rogocore.sdk.entity.IoTLocation
import javax.inject.Inject

class RepoLocation @Inject constructor() {
    val handler = FlowSdk.locationHandler()
    fun getLocationList(): List<IoTLocation> = handler.all.toList()

    fun getLocation(uuid: String?): IoTLocation? = handler.get(uuid)
    fun createLocation(
        label: String,
        type: String,
        callback: RequestResultCallback<IoTLocation>
    ) {
        handler.createLocation(
            label,
            type,
            callback
        )
    }

    fun editLocation(
        ioTLocation: IoTLocation,
        label: String,
        callback: RequestResultCallback<IoTLocation>
    ) {
        handler.updateLocation(
            ioTLocation.uuid,
            label,
            ioTLocation.desc,
            callback
        )
    }

    fun deleteLocation(
        uuid: String,
        callback: RequestResultCallback<Boolean>
    ) {
        handler.delete(
            uuid,
            callback
        )
    }

    fun getDefaultLocation(): String? = FlowSdk.getAppLocation()

    fun setDefaultLocation(uuid: String) {
        FlowSdk.setAppLocation(uuid)
    }
}