package com.example.thingsflow.module.repository

import com.example.thingflowsdk.core.FlowSdk
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback
import javax.inject.Inject

class RepoFlowBinding @Inject constructor() {
    private val TAG = "RepoFlowBinding"
    val handler = FlowSdk.flowBindingHandler()

    fun createFlowBinding(
        devId: String,
        sceneId: String,
        label: String,
        callback: SuccessStatusCallback
    ) {
        handler.createFlowBinding(
            devId,
            "sceneId",
            "bindingId",
            label,
            callback
        )
    }

    fun bindBoxes(
        devId: String,
        bindingId: String,
        boxes: ArrayList<FBox?>,
        callback: SuccessStatusCallback
    ) {
        handler.bindBoxesBinding(
            devId,
            bindingId,
            boxes,
            callback
        )
    }
}