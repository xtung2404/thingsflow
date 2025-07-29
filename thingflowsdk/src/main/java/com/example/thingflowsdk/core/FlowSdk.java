package com.example.thingflowsdk.core;

import android.content.Context;

import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback;
import rogo.iot.module.platform.ILogR;
import rogo.iot.module.rogocore.sdk.callback.SmartSdkConnectCallback;
import rogo.iot.module.rogocore.sdk.handler.ConfigWileDirectDeviceHandler;
import rogo.iot.module.rogocore.sdk.handler.GroupHandler;
import rogo.iot.module.rogocore.sdk.handler.LocationHandler;

public class FlowSdk {
    private static String TAG = "FlowSdk";
    protected static FlowSdkBaseHandler instance;

    protected static FlowSdkBaseHandler initInstance() {
        if (instance == null) {
            try {
                return new FlowSdkBaseHandlerImpl();
            } catch (Exception e) {
                ILogR.E(TAG, "initInstance", e.getMessage());
            }
        }
        return null;
    }

    public synchronized static void initV2(
        Context context,
        Boolean isForceStaging,
        String appKey,
        String appSecretKey
    ) {
        if (instance == null) {
            instance = initInstance();
            if (instance != null) {
                instance.initV2(
                    context,
                    isForceStaging,
                    appKey,
                    appSecretKey
                );
            }
        }
    }

    public synchronized static void signIn(
        String username,
        String email,
        String phoneNumber,
        String password,
        AuthRequestCallback callback
    ) {
        ((FlowSdkBaseHandler) instance).signIn(
            username,
            email,
            phoneNumber,
            password,
            callback
        );
    }

    public synchronized static void signUp(
            String username,
            String email,
            String phoneNumber,
            String password,
            AuthRequestCallback callback
    ) {
        ((FlowSdkBaseHandler) instance).signUp(
                username,
                email,
                phoneNumber,
                password,
                callback
        );
    }

    public synchronized static void forgot(
            String email,
            AuthRequestCallback callback
    ) {
        ((FlowSdkBaseHandler) instance).forgot(
                email,
                callback
        );
    }

    public synchronized static void updatePasswordOrVerifyAccount(
            String otp,
            String pwd,
            AuthRequestCallback callback
    ) {
        ((FlowSdkBaseHandler) instance).updatePasswordOrVerifyAccount(
                otp,
                pwd,
                callback
        );
    }

    public synchronized static void signOut(AuthRequestCallback callback) {
        ((FlowSdkBaseHandler) instance).signOut(callback);
    }

    public synchronized static void connectService(SmartSdkConnectCallback callback) {
        ((FlowSdkBaseHandler) instance).connectService(callback);
    }

    public synchronized static void closeServiceConnection() {
        ((FlowSdkBaseHandler) instance).closeServiceConnection();
    }

    public synchronized static void setAppLocation(String uuid) {
        ((FlowSdkBaseHandler) instance).setAppLocation(uuid);
    }

    public synchronized static String getAppLocation() {
        return ((FlowSdkBaseHandler) instance).getAppLocation();
    }

    public synchronized static LocationHandler locationHandler() {
        return ((FlowSdkBaseHandler) instance).locationHandler();
    }

    public synchronized static GroupHandler groupHandler() {
        return ((FlowSdkBaseHandler) instance).groupHandler();
    }

    public synchronized static ConfigWileDirectDeviceHandler configWileDirectDeviceHandler() {
        return ((FlowSdkBaseHandler) instance).configWileDirectDeviceHandler();
    }



}
