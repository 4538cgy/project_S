package com.uos.smsmsm.util.shareddate

import android.content.Context

// Apply ktlint
class PreferenceUtil(context: Context) {

    private val prefs = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)

    // clean-up
    fun getString(key: String, defValue: String) = prefs.getString(key, defValue).toString()

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }
}
