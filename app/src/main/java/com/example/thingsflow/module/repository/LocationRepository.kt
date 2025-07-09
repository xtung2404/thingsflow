package com.example.thingsflow.module.repository

import com.example.thingsflow.utils.UIState
import rogo.iot.module.platform.callback.RequestCallback
import rogo.iot.module.rogocore.sdk.SmartSdk
import rogo.iot.module.rogocore.sdk.entity.IoTLocation
import javax.inject.Inject

class LocationRepository @Inject constructor() {
    fun getLocationList(
        result: (MutableList<IoTLocation>) -> Unit
    ) {
        result.invoke(
            SmartSdk.locationHandler().all.toMutableList()
        )
    }

    fun createLocation(
        label: String,
        type: String,
        result: (UIState<IoTLocation>) -> Unit
    ) {
        SmartSdk.locationHandler().createLocation(
            label,
            type,
            object : RequestCallback<IoTLocation> {
                override fun onSuccess(p0: IoTLocation?) {
                    result.invoke(UIState.Success(p0))
                }

                override fun onFailure(p0: Int, p1: String?) {
                    result.invoke(UIState.Failure(p0, p1))
                }
            }
        )
    }
}