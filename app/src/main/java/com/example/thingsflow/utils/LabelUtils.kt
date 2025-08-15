package com.example.thingsflow.utils

import android.content.Context
import com.example.thingsflow.R
import rogo.iot.module.platform.define.IoTAttribute
import rogo.iot.module.platform.define.IoTDeviceType

fun getFragmentLabel(context: Context, destination: Int?): String =
    when(destination) {
        R.id.signInFragment -> context.resources.getString(R.string.sign_in)
        R.id.signUpFragment -> context.resources.getString(R.string.sign_up)
        R.id.locationManagementFragment -> context.resources.getString(R.string.select_location)
        R.id.homeFragment -> context.resources.getString(R.string.main_fragment)
        R.id.configWiFiFragment -> context.resources.getString(R.string.connect_to_wifi)
        R.id.identifyDeviceFragment -> context.resources.getString(R.string.add_gateway)
        else -> " "
    }

fun getDeviceTypeLabel(context: Context, type: Int): String =
    when(type) {
        IoTDeviceType.LIGHT -> context.resources.getString(R.string.light)
        IoTDeviceType.SWITCH -> context.resources.getString(R.string.switch_device_type)
        IoTDeviceType.PLUG -> context.resources.getString(R.string.plug)
        IoTDeviceType.CURTAINS -> context.resources.getString(R.string.curtains)
        IoTDeviceType.DOORLOCK -> context.resources.getString(R.string.doorlock)
        IoTDeviceType.CAMERA -> context.resources.getString(R.string.camera)
        IoTDeviceType.SPEAKER -> context.resources.getString(R.string.speaker)
        IoTDeviceType.MOTOR_CONTROLLER -> context.resources.getString(R.string.motor_controller)
        IoTDeviceType.AC_CONTROLLER -> context.resources.getString(R.string.ac_controller)
        IoTDeviceType.GATE -> context.resources.getString(R.string.gate)
        IoTDeviceType.GATEWAY -> context.resources.getString(R.string.gateway)
        IoTDeviceType.HEAT_SENSOR -> context.resources.getString(R.string.heat_sensor)
        IoTDeviceType.TEMP_SENSOR -> context.resources.getString(R.string.temp_sensor)
        IoTDeviceType.DOOR_SENSOR -> context.resources.getString(R.string.door_sensor)
        IoTDeviceType.SMOKE_SENSOR -> context.resources.getString(R.string.smoke_sensor)
        IoTDeviceType.MOTION_LUX_SENSOR -> context.resources.getString(R.string.motion_lux_sensor)
        IoTDeviceType.MOTION_SENSOR -> context.resources.getString(R.string.motion_sensor)
        IoTDeviceType.LUX_SENSOR -> context.resources.getString(R.string.lux_sensor)
        IoTDeviceType.PRESENSCE_SENSOR -> context.resources.getString(R.string.presence_sensor)
        else -> ""
    }

fun getAttrLabel(context: Context, attr: Int): String =
    when(attr) {
        IoTAttribute.ACT_ONOFF -> context.resources.getString(R.string.on_splash_off)
        IoTAttribute.ACT_OPEN_CLOSE -> context.resources.getString(R.string.open_splash_close)
        IoTAttribute.ACT_LOCK_UNLOCK -> context.resources.getString(R.string.lock_splash_unlock)
        IoTAttribute.EVT_BATTERY -> context.resources.getString(R.string.battery)
        IoTAttribute.ACT_BRIGHTNESS -> context.resources.getString(R.string.brightness)
        IoTAttribute.EVT_HUMID -> context.resources.getString(R.string.humid)
        IoTAttribute.EVT_TEMP -> context.resources.getString(R.string.temp)
        IoTAttribute.EVT_MOTION -> context.resources.getString(R.string.motion)
        IoTAttribute.EVT_LUX -> context.resources.getString(R.string.lux)
        IoTAttribute.EVT_SMOKE -> context.resources.getString(R.string.smoke)
        IoTAttribute.EVT_WALL_MOUNTED -> context.resources.getString(R.string.wall_mounted)
        else -> ""
    }