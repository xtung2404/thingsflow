package com.example.thingsflow

import android.app.Application
import com.example.thingflowsdk.core.FlowSdk
import dagger.hilt.android.HiltAndroidApp
import rogo.iot.module.platform.ILogR

@HiltAndroidApp
class ThingsFlowApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FlowSdk.initV2(
            this,
            false,
            "e4b75a6b23fc4f30bd5fab35436c6a90",
            "964e2c974f001a0468bf2734ce88e96652afff328886",
        )
//        SmartSdk.isForceStagingSvr = true
//        SmartSdk.isForceProduction = false
//        SmartSdk.isSupportModelDevelopment = true
//        SmartSdk.isV2DeviceBleConfig = true
//        AndroidIoTPlatform(this, false)
//        //init app key and app secret key
//        //ROGO-STAGING
//        SmartSdk().initV2(
//            "f78f5dd2fc594475a27bef7c2caf9ab4",
//            "41d96be770b2902f801b1689c5edae29c16a068e8f87"
//        )
        //ROGO-PRODUCTION
//        SmartSdk().initV2(
//            "e4b75a6b23fc4f30bd5fab35436c6a90",
//            "964e2c974f001a0468bf2734ce88e96652afff328886"
//        )
        //enable log
        ILogR.setEnablePrint(true)
    }
}