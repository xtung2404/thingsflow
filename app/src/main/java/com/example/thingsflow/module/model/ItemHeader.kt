package com.example.thingsflow.module.model

import java.util.UUID

data class ItemHeader(
    val id: String = UUID.randomUUID().toString(),
    var key: String = "",
    var value: String = ""
)
