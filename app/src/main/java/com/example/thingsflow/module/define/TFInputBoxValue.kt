package com.example.thingsflow.module.define

import rogo.iot.module.flowcommon.value.FInputValue

class TFInputBoxValue (
    val devId: String?,
    val elm: Int?,
    val inputType: TFInOutType,
    val input: FInputValue
)