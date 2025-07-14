package com.example.thingsflow.module.repository

import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTLocation
import javax.inject.Inject

class LocationRepository @Inject constructor() {
    fun getLocationList(): List<IoTLocation> = SmartSdk.locationHandler().all.toList()

    fun createLocation(
        label: String,
        type: String,
        callback: RequestCallback<IoTLocation>
    ) {
        SmartSdk.locationHandler().createLocation(
            label,
            type,
            callback
        )
    }

    fun editLocation(
        ioTLocation: IoTLocation,
        label: String,
        callback: RequestCallback<IoTLocation>
    ) {
        SmartSdk.locationHandler().updateLocation(
            ioTLocation.uuid,
            label,
            ioTLocation.desc,
            callback
        )
    }

    fun deleteLocation(
        uuid: String,
        callback: RequestCallback<Boolean>
    ) {
        SmartSdk.locationHandler().delete(
            uuid,
            callback
        )
    }
}