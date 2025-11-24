package com.example.thingsflow.module.model
data class ItemHeader(
    val id: Long = System.nanoTime(),
    var key: String = "",
    var value: String = ""
)
