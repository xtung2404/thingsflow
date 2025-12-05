package com.example.thingsflow.module.define

import android.content.Context
import com.example.thingsflow.R

enum class TFFieldType {
    INT,
    LONG,
    STRING,
    OBJECT;

    companion object {
        fun getFieldTypeLabel(context: Context, type: TFFieldType): String =
            when(type) {
                INT -> context.getString(R.string.int_type)
                LONG -> context.getString(R.string.long_type)
                STRING -> context.getString(R.string.string_type)
                OBJECT -> context.getString(R.string.object_type)
                else -> ""
            }
    }
}