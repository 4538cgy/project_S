package com.uos.smsmsm.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun isPermitted(context: Context, permissions: Array<String>): Boolean {

    for (permission in permissions) {
        val result = ContextCompat.checkSelfPermission(context, permission)
        if (result != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}