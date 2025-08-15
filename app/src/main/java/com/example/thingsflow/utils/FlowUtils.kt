package com.example.thingsflow.utils

import android.content.Context
import com.example.thingsflow.R
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventMqtt
import rogo.iot.module.platform.define.IoTAttribute
import rogo.iot.module.platform.define.IoTDeviceType

fun getSupportedBoxEvent(): List<Int> = listOf(
    FBoxEventType.FBOX_EVENT_TYPE_DEVICE,
    FBoxEventType.FBOX_EVENT_TYPE_MQTT
)

fun getSupportedDeviceType(): List<Int> = listOf(
    IoTDeviceType.LIGHT,
    IoTDeviceType.SWITCH,
    IoTDeviceType.PLUG,
    IoTDeviceType.CURTAINS,
    IoTDeviceType.DOORLOCK,
    IoTDeviceType.CAMERA,
    IoTDeviceType.SPEAKER,
    IoTDeviceType.MOTOR_CONTROLLER,
    IoTDeviceType.AC_CONTROLLER,
    IoTDeviceType.GATE,
    IoTDeviceType.GATEWAY,
    IoTDeviceType.HEAT_SENSOR,
    IoTDeviceType.TEMP_SENSOR,
    IoTDeviceType.DOOR_SENSOR,
    IoTDeviceType.SMOKE_SENSOR,
    IoTDeviceType.LUX_SENSOR,
    IoTDeviceType.PRESENSCE_SENSOR
)

fun getSupportedAttribue(): List<Int> = listOf(
    IoTAttribute.ACT_ONOFF,
    IoTAttribute.ACT_OPEN_CLOSE,
    IoTAttribute.ACT_LOCK_UNLOCK,
    IoTAttribute.EVT_BATTERY,
    IoTAttribute.EVT_HUMID,
    IoTAttribute.EVT_SMOKE,
    IoTAttribute.EVT_WALL_MOUNTED
)
fun getBoxEventTypeLabel(context: Context, type: Int): String =
    when(type) {
        FBoxEventType.FBOX_EVENT_TYPE_DEVICE ->  context.resources.getString(R.string.event_from_device)
        FBoxEventType.FBOX_EVENT_TYPE_MQTT ->  context.resources.getString(R.string.event_from_mqtt)
        else -> ""
    }
