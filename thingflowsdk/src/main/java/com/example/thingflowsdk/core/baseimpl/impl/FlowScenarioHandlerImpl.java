package com.example.thingflowsdk.core.baseimpl.impl;

import com.example.thingflowsdk.core.base.entity.TFFlowScenario;
import com.example.thingflowsdk.core.base.handler.FlowScenarioHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import rogo.iot.module.base.ILogR;
import rogo.iot.module.base.callback.RequestResultCallback;
import rogo.iot.module.flowcommon.box.FBox;
import rogo.iot.module.flowcommon.box.action.FBoxAction;
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice;
import rogo.iot.module.flowcommon.box.event.FBoxEvent;
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice;
import rogo.iot.module.flowcommon.type.FBoxType;
import rogo.iot.module.platform.feature.IoTFeature;
import rogo.iot.module.platform.invoking.IoTInvokingProperty;
import rogo.iot.module.rogocore.sdk.SmartSdk;
import rogo.iot.module.rogocore.sdk.callback.FeatureRequestCallback;
import rogo.iot.module.rogocore.sdk.callback.SuccessStatusCallback;

public class FlowScenarioHandlerImpl implements FlowScenarioHandler {
    private String TAG = "FlowScenarioHandlerImpl";

    @Override
    public void bindBoxesScenario(String sceneId, ArrayList<FBox> boxes, SuccessStatusCallback callback) {

    }

    @Override
    public void createFlowScenario(String label, RequestResultCallback<TFFlowScenario> callback) {

    }
}
