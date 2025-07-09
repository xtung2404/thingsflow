package com.example.thingsflow

import android.app.Application
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import rogo.iot.module.platform.ILogR
import rogo.iot.module.rogocore.app.AndroidIoTPlatform
import rogo.iot.module.rogocore.sdk.SmartSdk

@HiltAndroidApp
class ThingsFlowApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SmartSdk.isForceStagingSvr = true
        SmartSdk.isSupportModelDevelopment = true
        SmartSdk.isV2DeviceBleConfig = true
        AndroidIoTPlatform(this, false)
        //init app key and app secret key
        SmartSdk().initV2(
            "78c4807471bf498fa0dd943b1fd4ff9a",
            "060863465ebe478d569a565d439a76dda4fc79690c3b",
        )
        //enable log
        ILogR.setEnablePrint(true)
    }
}