package com.example.thingflowsdk.core.utils;

import com.google.gson.Gson;

import java.util.HashMap;

import rogo.iot.module.base.ILogR;
import rogo.iot.module.flowcommon.box.FBox;
import rogo.iot.module.flowcommon.box.action.FBoxActionCallHttp;
import rogo.iot.module.flowcommon.box.action.FBoxActionControlDevice;
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionDeviceState;
import rogo.iot.module.flowcommon.box.action.condition.FBoxActionConditionGeneral;
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice;
import rogo.iot.module.flowcommon.type.FBoxType;

public final class FBoxExecutor {
    private final static String TAG = "FBoxExecutor";
    private static final Gson gson = new Gson();

    public static int getTypeOfBox(FBox fBox) {
        if (fBox instanceof FBoxEventDevice) return FBoxType.EVT_FROM_DEVICE;
        if (fBox instanceof FBoxActionControlDevice) return FBoxType.ACT_CONTROL_DEVICE;
        return 0;
    }

    public static String generatedBoxEventData(FBox fBox) {
        HashMap<String, Object> data = new HashMap<>();
        if (fBox instanceof FBoxEventDevice) {
            FBoxEventDevice eventDevice = (FBoxEventDevice) fBox;
            data.put("eventTypes", getTypeOfBox(fBox));
            data.put("devType", eventDevice.getDevType());
            data.put("devId", eventDevice.getDevId());
            data.put("elms", eventDevice.getElms());
            data.put("eid", eventDevice.getEid());
            data.put("attrTypes", eventDevice.getElms());
            ILogR.D(TAG, "generatedBoxEventData:data", gson.toJson(data));
            return gson.toJson(data);
        }
        return "{}";
    }

    public static String generatedBoxActionData(FBox fBox) {
        HashMap<String, Object> data = new HashMap<>();
        if (fBox instanceof FBoxActionControlDevice) {
            FBoxActionControlDevice fBoxActionControlDevice = (FBoxActionControlDevice) fBox;
            data.put("devType", fBoxActionControlDevice.getDevType());
            data.put("attrType", fBoxActionControlDevice.getAttrType());
            data.put("targetControls", fBoxActionControlDevice.getTargetControls());
            return gson.toJson(data);
        } else if (fBox instanceof FBoxActionCallHttp) {
            FBoxActionCallHttp fBoxActionCallHttp = (FBoxActionCallHttp) fBox;
            data.put("url", fBoxActionCallHttp.getUrl());
            data.put("method", fBoxActionCallHttp.getMethod());
            data.put("body", fBoxActionCallHttp.getBody());
            data.put("headers", fBoxActionCallHttp.getHeaders());
            data.put("timeOutMs", fBoxActionCallHttp.getTimeoutMs());
            data.put("jsonFields", fBoxActionCallHttp.getJsonFields());
            return gson.toJson(data);
        }
        else if (fBox instanceof FBoxActionConditionDeviceState) {
            FBoxActionConditionDeviceState fBoxActionConditionDeviceState = (FBoxActionConditionDeviceState) fBox;
            data.put("devId", fBoxActionConditionDeviceState.getDevId());
            data.put("eid", fBoxActionConditionDeviceState.getEid());
            data.put("elm", fBoxActionConditionDeviceState.getElm());
            data.put("attrType", fBoxActionConditionDeviceState.getAttrType());
            data.put("isInputFromPreviousBox", fBoxActionConditionDeviceState.isInputFromPreviousBox());
            data.put("condition", fBoxActionConditionDeviceState.getCondition());
            data.put("comparingValue", fBoxActionConditionDeviceState.getComparingValue());
            return gson.toJson(data);
        } else if (fBox instanceof FBoxActionConditionGeneral) {
            FBoxActionConditionGeneral fBoxActionConditionGeneral = (FBoxActionConditionGeneral) fBox;
            data.put("valueType", fBoxActionConditionGeneral.getValueType());
            data.put("condition", fBoxActionConditionGeneral.getCondition());
            data.put("comparingValue", fBoxActionConditionGeneral.getComparingValue());
            data.put("comparedValue", fBoxActionConditionGeneral.getComparedValue());
        }
        return "{}";
    }
}
