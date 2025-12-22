package com.example.thingflowsdk.core.baseimpl.impl;

import com.example.thingflowsdk.core.base.handler.FlowBindingHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import rogo.iot.module.base.ILogR;
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

public class FlowBindingHandlerImpl implements FlowBindingHandler {
    private String TAG = "FlowBindingHandlerImpl";

    private final Gson gson = new Gson();
    @Override
    public void createFlowBinding(
            String devId,
            String sceneId,
            String bindingId,
            String label,
            SuccessStatusCallback callback) {
        ILogR.D(TAG, "createFlowBinding:devId", devId, label);
        SmartSdk.featureHandler().runFeatureMethod(
                false,
                devId,
                IoTFeature.BUILTIN_SERVICE_FLOW,
                "createFlowBinding",
                new IoTFeature.FeatureValue() {
                    @IoTInvokingProperty("flowSceneId")
                    private String flowSceneId = sceneId;
                    @IoTInvokingProperty("flowBindingId")
                    private String flowBindingId = bindingId;

                },
                30000,
                new FeatureRequestCallback() {

                    @Override
                    public void onRequestDelivered(int i, String s) {
                        ILogR.D(TAG, "onRequestDelivered", i, s);
                    }

                    @Override
                    public void onRequestRepliedSuccess(int i, String s) {
                        ILogR.D(TAG, "onRequestRepliedSuccess", i, s);
                    }

                    @Override
                    public void onRequestFailure(int i, String s) {
                        ILogR.D(TAG, "onRequestFailure", i, s);
                    }
                }
        );
    }

    @Override
    public void bindBoxesBinding(
            String devId,
            String bindingId,
            ArrayList<FBox> boxes,
            SuccessStatusCallback callback
    ) {
        ArrayList<FBox> eventBoxes = new ArrayList<>();
        ArrayList<FBox> actionBoxes = new ArrayList<>();
        for(FBox box: boxes) {
            if (box instanceof FBoxEvent) {
                eventBoxes.add(box);
            } else {
                actionBoxes.add(box);
            }
        }
        bindBoxEvent(
                devId,
                bindingId,
                eventBoxes,
                new SuccessStatusCallback() {
                    @Override
                    public void onSuccess() {
                        bindOtherBoxes(
                                devId,
                                bindingId,
                                actionBoxes,
                                new SuccessStatusCallback() {
                                    @Override
                                    public void onSuccess() {
                                        callback.onSuccess();
                                    }

                                    @Override
                                    public void onFailure(int errorCode, String msg) {
                                        callback.onFailure(errorCode, msg);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

    }


    private void bindBoxEvent(
            String devId,
            String bindingId,
            ArrayList<FBox> eventBoxes,
            SuccessStatusCallback callback
    ) {
        int size = eventBoxes.size();

        String[] evtBoxIds = new String[size];
        Integer[] evtEventTypes = new Integer[size];
        String[] evtTargetIds = new String[size];
        String[] evtEventDatas = new String[size];

        for (int i = 0; i < size; i++) {
            FBox eventBox = eventBoxes.get(i);
            evtBoxIds[i] = eventBox.getId();
            evtEventTypes[i] = getTypeOfBox(eventBox);
            evtTargetIds[i] = (eventBox instanceof FBoxEvent) ? ((FBoxEventDevice) eventBox).getTargetSegId() : null;
            evtEventDatas[i] = generatedBoxEventData(eventBox);
        }

        ILogR.D(TAG, "onBindBoxEvent", new Gson().toJson(evtEventDatas));
        SmartSdk.featureHandler().runFeatureMethod(
                false,
                devId,
                IoTFeature.BUILTIN_SERVICE_FLOW,
                "bindBoxEvent",
                new IoTFeature.FeatureValue() {
                    @IoTInvokingProperty("flowBindingId")
                    private String flowBindingId = bindingId;
                    @IoTInvokingProperty("eventBoxIds")
                    private String[] eventBoxIds = evtBoxIds;
                    @IoTInvokingProperty("eventType")
                    private Integer[] eventType = evtEventTypes;
                    @IoTInvokingProperty("targetSegId")
                    private String[] targetSegId = evtTargetIds;
                    @IoTInvokingProperty("eventData")
                    private String[] eventData = evtEventDatas;
                },
                30000,
                new FeatureRequestCallback() {

                    @Override
                    public void onRequestDelivered(int i, String s) {
                        ILogR.D(TAG, "onBindBoxEvent:onRequestDelivered", i, s);
                    }

                    @Override
                    public void onRequestRepliedSuccess(int i, String s) {
                        ILogR.D(TAG, "onBindBoxEvent:onRequestRepliedSuccess", i, s);
                        callback.onSuccess();
                    }

                    @Override
                    public void onRequestFailure(int i, String s) {
                        ILogR.D(TAG, "onBindBoxEvent:onRequestFailure", i, s);
                        callback.onFailure(i, s);
                    }
                }
        );
    }

    private void bindOtherBoxes(
            String devId,
            String bindingId,
            ArrayList<FBox> boxes,
            SuccessStatusCallback callback
    ) {
        int size = boxes.size();
        String[] actBoxIds = new String[size];
        Integer[] actTypes = new Integer[size];
        String[] actSegIds = new String[size];
        String[] actPosSegIds = new String[size];
        String[] actNegSegIds = new String[size];
        String[] actDatas = new String[size];

        for (int i = 0; i < size; i++) {
            FBoxAction actionBox = (FBoxAction) boxes.get(i);
            actBoxIds[i] = actionBox.getId();
            actTypes[i] = getTypeOfBox(actionBox);
            actSegIds[i] = actionBox.getSegId();
            actPosSegIds[i] = actionBox.getPositiveSegId();
            actNegSegIds[i] = actionBox.getNegativeSegId();
            actDatas[i] = generatedBoxActionData(actionBox);
        }
        SmartSdk.featureHandler().runFeatureMethod(
                false,
                devId,
                IoTFeature.BUILTIN_SERVICE_FLOW,
                "bindOtherBoxes",
                new IoTFeature.FeatureValue() {
                    @IoTInvokingProperty("flowBindingId")
                    private String flowBindingId = bindingId;
                    @IoTInvokingProperty("boxIds")
                    private String[] boxIds = actBoxIds;

                    @IoTInvokingProperty("actionTypes")
                    private Integer[] actionTypes = actTypes;
                    @IoTInvokingProperty("segIds")
                    private String[] segIds = actSegIds;
                    @IoTInvokingProperty("positiveSegId")
                    private String[] positiveSegId = actPosSegIds;

                    @IoTInvokingProperty("negativeSegId")
                    private String[] negativeSegId = actNegSegIds;

                    @IoTInvokingProperty("boxData")
                    private String[] boxData = actDatas;

                },
                30000,
                new FeatureRequestCallback() {
                    @Override
                    public void onRequestDelivered(int i, String s) {
                        ILogR.D(TAG, "onBindBoxAction:onRequestDelivered", i, s);
                    }
                    @Override
                    public void onRequestRepliedSuccess(int i, String s) {
                        ILogR.D(TAG, "onBindBoxAction:onRequestRepliedSuccess", i, s);
                        callback.onSuccess();
                    }
                    @Override
                    public void onRequestFailure(int errorCode, String msg) {
                        ILogR.D(TAG, "onBindBoxAction:onRequestFailure", errorCode, msg);
                        callback.onFailure(errorCode, msg);
                    }
                }
        );
    }

    private int getTypeOfBox(FBox fBox) {
        if (fBox instanceof FBoxEventDevice) return FBoxType.EVT_FROM_DEVICE;
        if (fBox instanceof FBoxActionControlDevice) return FBoxType.ACT_CONTROL_DEVICE;
        return 0;
    }

    private String generatedBoxEventData(FBox fBox) {
        HashMap<String, Object> data = new HashMap<>();
        if (fBox instanceof FBoxEventDevice) {
            FBoxEventDevice eventDevice = (FBoxEventDevice) fBox;
            data.put("eventTypes", getTypeOfBox(fBox));
            data.put("devType", eventDevice.getDevType());
            data.put("devId", eventDevice.getDevId());
            data.put("elms", eventDevice.getElms());
            data.put("eid", eventDevice.getEid());
            data.put("attrTypes", eventDevice.getElms());
            return gson.toJson(data);
        }
        return "{}";
    }

    private String generatedBoxActionData(FBox fBox) {
        HashMap<String, Object> data = new HashMap<>();
        if (fBox instanceof FBoxActionControlDevice) {
            FBoxActionControlDevice actionControlDevice = (FBoxActionControlDevice) fBox;
            data.put("devType", actionControlDevice.getDevType());
            data.put("attrType", actionControlDevice.getAttrType());
            data.put("targetControls", actionControlDevice.getTargetControls());
            return gson.toJson(data);
        }
        return "{}";
    }
}
