package com.uos.smsmsm.util

import android.os.SystemClock
import android.view.View

abstract class OneClickListener : View.OnClickListener {
    override fun onClick(v: View) {
        if (started || SystemClock.elapsedRealtime() - lastClickEndTime < 500) {
            return
        }
        try {
            started = true
            onOneClick(v)
        } finally {
            started = false
            lastClickEndTime = SystemClock.elapsedRealtime()
        }
    }

    protected abstract fun onOneClick(v: View?)

    companion object {
        private var started = false
        private var lastClickEndTime: Long = 0
    }
}