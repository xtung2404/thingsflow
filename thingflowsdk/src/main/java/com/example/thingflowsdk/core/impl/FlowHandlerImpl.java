package com.example.thingflowsdk.core.impl;

import com.example.thingflowsdk.core.handler.FlowHandler;

import java.util.ArrayList;

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

public class FlowHandlerImpl implements FlowHandler {
    private String TAG = "FlowHandlerImpl";
    @Override
    public void createFlowScenario() {

    }

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

                    }

                    @Override
                    public void onRequestRepliedSuccess(int i, String s) {

                    }

                    @Override
                    public void onRequestFailure(int i, String s) {

                    }
                }
        );
    }

    @Override
    public void bindBoxes(String devId, String bindingId, ArrayList<FBox> boxes, SuccessStatusCallback callback) {
        ArrayList<FBox> eventBoxes = new ArrayList<>();
        ArrayList<FBox> actionBoxes = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i) instanceof FBoxEvent) {
                eventBoxes.add(boxes.get(i));
            } else {
                actionBoxes.add(boxes.get(i));
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

                                    }

                                    @Override
                                    public void onFailure(int i, String s) {

                                    }
                                }
                        );
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

    }


    public void bindBoxEvent(
            String devId,
            String bindingId,
            ArrayList<FBox> eventBoxes,
            SuccessStatusCallback callback
    ) {
        ArrayList<Integer> evtTypes = new ArrayList<>();
        ArrayList<String> boxIds = new ArrayList<>();
        ArrayList<String> targetId = new ArrayList<>();
        ArrayList<String> eventDatas = new ArrayList<>();
        for (int i = 0; i < eventBoxes.size(); i++) {
            boxIds.add(i, eventBoxes.get(i).getId());
            eventDatas.add(i, null);
            if (eventBoxes.get(i) instanceof FBoxEvent) {
                String boxTargetId = ((FBoxEventDevice) eventBoxes.get(i)).getTargetSegId();
                targetId.add(i, boxTargetId);
            }
            if (eventBoxes.get(i) instanceof FBoxEventDevice) {
                evtTypes.add(i, FBoxType.EVT_FROM_DEVICE);
            }
        }
        SmartSdk.featureHandler().runFeatureMethod(
                false,
                devId,
                IoTFeature.BUILTIN_SERVICE_FLOW,
                "bindBoxEvent",
                new IoTFeature.FeatureValue() {
                    @IoTInvokingProperty("flowBindingId")
                    private String flowBindingId = bindingId;
                    @IoTInvokingProperty("eventBoxIds")
                    private String[] eventBoxIds = boxIds.toArray(new String[eventBoxes.size()]);
                    @IoTInvokingProperty("eventType")
                    private Integer[] eventType = evtTypes.toArray(new Integer[eventBoxes.size()]);
                    @IoTInvokingProperty("targetSegId")
                    private String[] targetSegId = targetId.toArray(new String[eventBoxes.size()]);
                    @IoTInvokingProperty("eventData")
                    private String[] eventData = eventDatas.toArray(new String[eventBoxes.size()]);


                },
                30000,
                new FeatureRequestCallback() {

                    @Override
                    public void onRequestDelivered(int i, String s) {

                    }

                    @Override
                    public void onRequestRepliedSuccess(int i, String s) {
                        callback.onSuccess();
                    }

                    @Override
                    public void onRequestFailure(int i, String s) {
                        callback.onFailure(i, s);
                    }
                }
        );
    }
    public void bindOtherBoxes(
            String devId,
            String bindingId,
            ArrayList<FBox> boxes,
            SuccessStatusCallback callback
    ) {
        ArrayList<Integer> actTypes = new ArrayList<>();
        ArrayList<String> actBoxIds = new ArrayList<>();
        ArrayList<String> actSegIds = new ArrayList<>();
        ArrayList<String> actPosSegIds = new ArrayList<>();
        ArrayList<String> actNegSegIds = new ArrayList<>();
        ArrayList<String> actDatas = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            actBoxIds.add(i, boxes.get(i).getId());
            actSegIds.add(i, (((FBoxAction) boxes.get(i)).getSegId()));
            actPosSegIds.add(i, (((FBoxAction) boxes.get(i)).getPositiveSegId()));
            actNegSegIds.add(i, (((FBoxAction) boxes.get(i)).getNegativeSegId()));
            if (boxes.get(i) instanceof FBoxActionControlDevice) {
                actTypes.add(i, FBoxType.ACT_CONTROL_DEVICE);
                actDatas.add(i, null);
            }
        }
        int size = boxes.size();
        SmartSdk.featureHandler().runFeatureMethod(
                false,
                devId,
                IoTFeature.BUILTIN_SERVICE_FLOW,
                "bindOtherBoxes",
                new IoTFeature.FeatureValue() {
                    @IoTInvokingProperty("flowBindingId")
                    private String flowBindingId = bindingId;
                    @IoTInvokingProperty("boxIds")
                    private String[] boxIds = actBoxIds.toArray(new String[size]);

                    @IoTInvokingProperty("actionTypes")
                    private Integer[] actionTypes = actTypes.toArray(new Integer[size]);
                    @IoTInvokingProperty("segIds")
                    private String[] segIds = actSegIds.toArray(new String[size]);
                    @IoTInvokingProperty("positiveSegId")
                    private String[] positiveSegId = actPosSegIds.toArray(new String[size]);

                    @IoTInvokingProperty("negativeSegId")
                    private String[] negativeSegId = actNegSegIds.toArray(new String[size]);

                    @IoTInvokingProperty("boxData")
                    private String[] boxData = actDatas.toArray(new String[size]);

                },
                30000,
                new FeatureRequestCallback() {


                    @Override
                    public void onRequestDelivered(int i, String s) {

                    }

                    @Override
                    public void onRequestRepliedSuccess(int i, String s) {

                    }

                    @Override
                    public void onRequestFailure(int i, String s) {

                    }
                }
        );
    }
}
