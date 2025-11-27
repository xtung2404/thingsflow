package com.example.thingsflow.utils

import android.content.Context
import android.view.View
import com.example.thingsflow.R
import rogo.iot.module.flowcommon.box.FBox
import rogo.iot.module.flowcommon.box.event.FBoxEvent
import rogo.iot.module.flowcommon.box.event.FBoxEventDevice
import rogo.iot.module.flowcommon.box.event.FBoxEventMqtt
import rogo.iot.module.flowcommon.type.FTypeEvent
import rogo.iot.module.platform.define.IoTAttribute
import rogo.iot.module.platform.define.IoTDeviceType


fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}



fun getSupportedBoxEvent(): List<Int> = listOf(
    FTypeEvent.EVT_FROM_DEVICE
)

fun getControlableDeviceType(attr: Int): List<Int> {
    when(attr) {
        IoTAttribute.ACT_ONOFF -> {
            return getOnOffDeviceType()
        }

        IoTAttribute.ACT_OPEN_CLOSE -> {
            return getOpenCloseDeviceType()
        }

        IoTAttribute.ACT_LOCK_UNLOCK -> {
            return getLockUnlockDeviceType()
        }
    }
    return listOf()
}

fun getOnOffDeviceType(): List<Int> = listOf(
    IoTDeviceType.ALL,
    IoTDeviceType.LIGHT,
    IoTDeviceType.SWITCH,
    IoTDeviceType.PLUG,
    IoTDeviceType.SENSOR_PRESENCE
)

fun getOpenCloseDeviceType(): List<Int> = listOf(
    IoTDeviceType.ALL,
    IoTDeviceType.CURTAINS,
    IoTDeviceType.MOTOR_CONTROLLER,
    IoTDeviceType.GATE
)

fun getLockUnlockDeviceType(): List<Int> = listOf(
    IoTDeviceType.ALL,
    IoTDeviceType.DOORLOCK
)

fun getSupportedDeviceType(): List<Int> = listOf(
    -1,
    IoTDeviceType.LIGHT,
    IoTDeviceType.SWITCH,
    IoTDeviceType.PLUG,
    IoTDeviceType.CURTAINS,
    IoTDeviceType.DOORLOCK,
    IoTDeviceType.CAMERA,
    IoTDeviceType.SPEAKER,
    IoTDeviceType.MOTOR_CONTROLLER,
//    IoTDeviceType.AC_CONTROLLER,
    IoTDeviceType.GATE,
    IoTDeviceType.GATEWAY,
//    IoTDeviceType.HEAT_SENSOR,
//    IoTDeviceType.TEMP_SENSOR,
//    IoTDeviceType.DOOR_SENSOR,
//    IoTDeviceType.SMOKE_SENSOR,
//    IoTDeviceType.LUX_SENSOR,
//    IoTDeviceType.PRESENSCE_SENSOR
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
        FTypeEvent.EVT_FROM_DEVICE ->  context.resources.getString(R.string.event_from_device)
        else -> ""
    }

fun getSupportedBoxType(): List<Int> = listOf(
    FTypeBox.TYPE_BOX_ACTION,
    FTypeBox.TYPE_BOX_CONDITION
)
