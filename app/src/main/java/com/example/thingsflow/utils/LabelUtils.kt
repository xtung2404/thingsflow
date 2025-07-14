package com.example.thingsflow.utils

import android.content.Context
import com.example.thingsflow.R

fun getFragmentLabel(context: Context, destination: Int?): String =
    when(destination) {
        R.id.signInFragment -> context.resources.getString(R.string.sign_in)
        R.id.signUpFragment -> context.resources.getString(R.string.sign_up)
        R.id.locationManagementFragment -> context.resources.getString(R.string.select_location)
        else -> " "
    }