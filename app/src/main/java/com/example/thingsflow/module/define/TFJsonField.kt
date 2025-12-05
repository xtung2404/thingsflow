package com.example.thingsflow.module.define

import java.util.UUID

class TFJsonField(
    val id: String = UUID.randomUUID().toString(),
    val flowSceneId: String,
    val rootBoxId: String,
    val label: String,
    val jsonPath: String,
    val type: TFFieldType,
    val fields: MutableList<TFJsonField>
)