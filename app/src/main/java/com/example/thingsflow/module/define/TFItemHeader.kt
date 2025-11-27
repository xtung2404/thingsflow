package com.example.thingsflow.module.define

import java.util.UUID

data class TFItemHeader(
    val id: String = UUID.randomUUID().toString(),
    var key: String = "",
    var value: String = ""
)
