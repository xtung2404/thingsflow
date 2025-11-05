package com.example.thingflowsdk.core.handler;

import java.util.ArrayList;

import rogo.iot.module.flowcommon.box.FBox;
import rogo.iot.module.flowcommon.box.event.FBoxEvent;
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback;

public interface FlowHandler {
    void createFlowScenario();

    void createFlowBinding(
        String devId,
        String sceneId,
        String bindingId,
        String label,
        SuccessStatusCallback callback
    );

    void bindBoxes(
        String devId,
        String bindingId,
        ArrayList<FBox> boxes,
        SuccessStatusCallback callback
    );
}
