package com.example.thingflowsdk.core;

import android.content.Context;

import rogo.iot.module.cloudapi.auth.callback.AuthRequestCallback;
import rogo.iot.module.rogocore.sdk.callback.SmartSdkConnectCallback;
import rogo.iot.module.rogocore.sdk.entity.IoTProductModel;
import rogo.iot.module.rogocore.sdk.handler.ConfigWileDirectDeviceHandler;
import rogo.iot.module.rogocore.sdk.handler.GroupHandler;
import rogo.iot.module.rogocore.sdk.handler.LocationHandler;

interface FlowSdkBaseHandler {
    void initV2(Context context, Boolean isForceStaging, String appKey, String appSecretKey);

    void signIn(String username, String email, String password, String phoneNumber, AuthRequestCallback callback);

    void signUp(String username, String email, String password, String phoneNumber, AuthRequestCallback callback);

    void forgot(String email, AuthRequestCallback callback);

    void updatePasswordOrVerifyAccount(String otp, String pwd, AuthRequestCallback callback);

    void signOut(AuthRequestCallback callback);

    void connectService(SmartSdkConnectCallback callback);

    void closeServiceConnection();

    void setAppLocation(String uuid);

    String getAppLocation();

    IoTProductModel getProductModel(String productId);

    LocationHandler locationHandler();

    GroupHandler groupHandler();
    ConfigWileDirectDeviceHandler configWileDirectDeviceHandler();

}
