package com.example.thingsflow.module.define

import java.util.UUID

data class TFJsonField(
    val id: String = UUID.randomUUID().toString(),
    val flowSceneId: String?= null,
    val rootBoxId: String?= null,
    var label: String,
    var jsonPath: String,
    var type: TFPrimitiveType,
    val fields: MutableList<TFJsonField> = mutableListOf<TFJsonField>()
)