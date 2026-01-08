package com.example.thingsflow.ui.adapter

import com.example.thingsflow.module.define.TFInputBoxValue

interface AdapterItem {
    data class HeaderItem(val title: String): AdapterItem
    data class ContentItem(val data: TFInputBoxValue?): AdapterItem
}