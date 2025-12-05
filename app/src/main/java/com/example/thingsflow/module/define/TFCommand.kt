package com.example.thingsflow.module.define

import rogo.iot.module.base.define.IoTAttribute
import rogo.iot.module.platform.define.IoTCmdConst

enum class TFCommand(
    val cmd: IntArray
) {
    /**
    * on off
    * */
//    ON_OFF(intArrayOf(IoTAttribute.ACT_ONOFF)),
    ON(intArrayOf(IoTAttribute.ACT_ONOFF, IoTCmdConst.POWER_ON)),
    OFF(intArrayOf(IoTAttribute.ACT_ONOFF, IoTCmdConst.POWER_OFF)),

    /**
     * open close
     */
//    OPEN_CLOSE(intArrayOf(IoTAttribute.ACT_OPEN_CLOSE)),
    OPEN(intArrayOf(IoTAttribute.ACT_OPEN_CLOSE, IoTCmdConst.OPENCLOSE_MODE_OPEN)),
    CLOSE(intArrayOf(IoTAttribute.ACT_OPEN_CLOSE, IoTCmdConst.OPENCLOSE_MODE_CLOSE)),
    STOP(intArrayOf(IoTAttribute.ACT_OPEN_CLOSE, IoTCmdConst.OPENCLOSE_MODE_STOP)),

    /**
     * lock unlock
     */
//    LOCK_UNLOCK(intArrayOf(IoTAttribute.ACT_LOCK_UNLOCK)),
    LOCK(intArrayOf(IoTAttribute.ACT_LOCK_UNLOCK, IoTCmdConst.DOOR_LOCKED)),
    UNLOCK(intArrayOf(IoTAttribute.ACT_LOCK_UNLOCK, IoTCmdConst.DOOR_UNLOCKED)),

    /**
    * presence
    */
//    PRESENCE_UNPRESENCE(intArrayOf(IoTAttribute.EVT_PRESENCE)),
    PRESENCE(intArrayOf(IoTAttribute.EVT_PRESENCE, IoTCmdConst.PRESENCE_STATUS_DETECTED));

    companion object {
        fun getCmdList() = listOf<TFCommand>(
            ON,
            OFF,
            OPEN,
            CLOSE,
            STOP,
            LOCK,
            UNLOCK
        )
        fun getOnOffCmd() = listOf<TFCommand>(
            ON,
            OFF
        )

        fun getOpenCloseCmd() = listOf<TFCommand>(
            OPEN,
            CLOSE,
            STOP
        )

        fun getLockUnlockCmd() = listOf<TFCommand>(
            LOCK,
            UNLOCK
        )

        fun getCmdByAttr(attr: Int): List<TFCommand> {
            return getCmdList().filter { it.cmd.first() == attr }
        }
    }


}