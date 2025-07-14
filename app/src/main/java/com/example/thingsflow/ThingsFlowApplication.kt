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
            "f78f5dd2fc594475a27bef7c2caf9ab4",
            "41d96be770b2902f801b1689c5edae29c16a068e8f87"
        )
        //enable log
        ILogR.setEnablePrint(true)
    }
}