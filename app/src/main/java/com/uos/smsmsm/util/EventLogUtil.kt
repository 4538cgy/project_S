package com.uos.smsmsm.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.uos.smsmsm.BuildConfig
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class EventLogUtil {
    fun sendScreenName(className: String, context: Context) {
        logScreenEvent(getScreenName(className), null, context)
    }
    fun sendScreenName(className: String, bundle: Bundle , context: Context) {
        logScreenEvent(getScreenName(className), bundle, context)
    }

    fun sendActionEvent(eventName: String, context: Context){
        logScreenEvent(eventName, null, context)
    }
    fun sendActionEvent(eventName: String, bundle: Bundle, context: Context){
        logScreenEvent(eventName, bundle, context)
    }
    private fun getScreenName(className: String,): String {
        var view = className.replace("Activity", "").replace("Fragment", "")
        val matcher: Matcher = Pattern.compile("([A-Z])").matcher(view)
        val upperList = ArrayList<String>()
        while (matcher.find()) {
            upperList.add(matcher.group().toString())
        }
        for (i in upperList.indices) {
            view = view.replace(upperList[i], "_" + upperList[i])
        }
        return  "view" + view.toLowerCase()
    }
    // 파이어베이스에 이벤트 등록. 단 디버그 모드일떄는 등록이 안됨
    private fun logScreenEvent(name: String, bundle: Bundle?, context: Context?) {
        var sendName = name
        if (BuildConfig.DEBUG) return
        if (context == null) return
        if (name.length > 40) {
            sendName = name.substring(0, 40)
        }

        FirebaseAnalytics.getInstance(context).logEvent(sendName, bundle)
    }
}