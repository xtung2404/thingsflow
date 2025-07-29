package com.example.thingflowsdk.core;

import android.content.Context;

import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback;
import rogo.iot.module.platform.ILogR;
import rogo.iot.module.rogocore.app.AndroidIoTPlatform;
import rogo.iot.module.rogocore.sdk.SmartSdk;
import rogo.iot.module.rogocore.sdk.callback.SmartSdkConnectCallback;
import rogo.iot.module.rogocore.sdk.handler.ConfigWileDirectDeviceHandler;
import rogo.iot.module.rogocore.sdk.handler.GroupHandler;
import rogo.iot.module.rogocore.sdk.handler.LocationHandler;

public class FlowSdkBaseHandlerImpl implements FlowSdkBaseHandler{
    private String TAG = "FlowSdkBaseHandlerImpl";
    @Override
    public void initV2(
            Context context,
            Boolean isForceStaging,
            String appKey,
            String appSecretKey
    ) {
        SmartSdk.isForceStagingSvr = isForceStaging;
        SmartSdk.isForceProduction = !isForceStaging;
        SmartSdk.isSupportModelDevelopment = true;
        SmartSdk.isV2DeviceBleConfig = true;
        new AndroidIoTPlatform(context, false);
        try {
            new SmartSdk().initV2(appKey, appSecretKey);
        } catch (Exception e) {
            ILogR.D(TAG, "initV2:exception ", e.getMessage());
        }
    }

    @Override
    public void signIn(
            String username,
            String email,
            String password,
            String phoneNumber,
            AuthRequestCallback callback
    ) {
        SmartSdk.signIn(
            username,
            email,
            phoneNumber,
            password,
            callback
        );
    }

    @Override
    public void signUp(
            String username,
            String email,
            String password,
            String phoneNumber,
            AuthRequestCallback callback
    ) {
        SmartSdk.signUp(
            username,
            email,
            phoneNumber,
            password,
            callback
        );
    }

    @Override
    public void forgot(
            String email,
            AuthRequestCallback callback
    ) {
        SmartSdk.forgot(email, callback);
    }

    @Override
    public void updatePasswordOrVerifyAccount(
            String otp,
            String pwd,
            AuthRequestCallback callback
    ) {
        SmartSdk.updatePasswordOrVerifyAccount(otp, pwd, callback);
    }

    @Override
    public void signOut(AuthRequestCallback callback) {
        SmartSdk.signOut(callback);
    }

    @Override
    public void connectService(SmartSdkConnectCallback callback) {
        SmartSdk.connectService(callback);
    }

    @Override
    public void closeServiceConnection() {
        SmartSdk.closeConnectionService();
    }

    @Override
    public void setAppLocation(String uuid) {
        SmartSdk.setAppLocation(uuid);
    }

    @Override
    public String getAppLocation() {
        return SmartSdk.getAppLocation();
    }

    @Override
    public LocationHandler locationHandler() {
        return SmartSdk.locationHandler();
    }

    @Override
    public GroupHandler groupHandler() {
        return SmartSdk.groupHandler();
    }

    @Override
    public ConfigWileDirectDeviceHandler configWileDirectDeviceHandler() {
        return SmartSdk.configWileDirectDeviceHandler();
    }
}
