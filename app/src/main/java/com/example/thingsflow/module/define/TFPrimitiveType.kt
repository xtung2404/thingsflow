package com.example.thingsflow.module.define

import android.content.Context
import android.graphics.Color
import com.example.thingsflow.R

enum class TFPrimitiveType {
    INT,
    LONG,
    STRING,
    OBJECT;

    companion object {
        fun getFieldTypeLabel(context: Context, type: TFPrimitiveType): String =
            when(type) {
                INT -> context.getString(R.string.int_type)
                LONG -> context.getString(R.string.long_type)
                STRING -> context.getString(R.string.string_type)
                OBJECT -> context.getString(R.string.object_type)
                else -> ""
            }

        fun getFieldTypeColor(context: Context, type: TFPrimitiveType): Int =
            when(type) {
                OBJECT -> context.getColor(R.color.liga)
                INT -> context.getColor(R.color.green)
                LONG -> context.getColor(R.color.green)
                STRING -> context.getColor(R.color.green)
            }
    }
}