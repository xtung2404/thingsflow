package com.example.thingflowsdk.core.base.handler;

import com.example.thingflowsdk.core.base.entity.TFFlowScenario;

import java.util.ArrayList;

import rogo.iot.module.base.callback.RequestResultCallback;
import rogo.iot.module.flowcommon.box.FBox;
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback;

public interface FlowBindingHandler {
    void createFlowBinding(
        String devId,
        String sceneId,
        String bindingId,
        String label,
        SuccessStatusCallback callback
    );

    void bindBoxesBinding(
        String devId,
        String bindingId,
        ArrayList<FBox> boxes,
        SuccessStatusCallback callback
    );
}
