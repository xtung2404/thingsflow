package com.example.thingsflow.module.define

import java.util.UUID

class TFOuputJson(
    val uuid: String = UUID.randomUUID().toString(),
    val flowSceneId: String?= null,
    var jsonFields: ArrayList<TFJsonField>
)