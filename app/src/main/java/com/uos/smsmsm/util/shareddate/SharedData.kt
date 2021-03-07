package com.uos.smsmsm.util.shareddate

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SharedData : Application() {
    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}