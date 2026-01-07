package com.example.thingflowsdk.core.base.handler;

import com.example.thingflowsdk.core.base.entity.TFFlowScenario;

import java.util.ArrayList;

import rogo.iot.module.base.callback.RequestResultCallback;
import rogo.iot.module.flowcommon.box.FBox;
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback;

public interface FlowScenarioHandler {
    void createFlowScenario(
            String flowSceneId,
            String devId,
            String label,
            String desc,
            RequestResultCallback<TFFlowScenario> callback
    );

    void bindBoxesScenario(
            String sceneId,
            String devId,
            ArrayList<FBox> boxes,
            SuccessStatusCallback callback
    );

    void deleteScene(
            String flowSceneId
    );
}
