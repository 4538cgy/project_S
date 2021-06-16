package com.uos.smsmsm.util.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

interface ActivityManager{

    fun addActivity(activity : AppCompatActivity)
    fun removeActivity(activity: AppCompatActivity)
    fun getActivitySize() : Int
    fun removeAllBehindActivity(intent : Intent)
    fun removeAllBehindActivity(activity: AppCompatActivity)
    fun removeAllBehindActivity()
}