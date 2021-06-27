package com.uos.smsmsm.util

import android.os.SystemClock
import android.view.View

abstract class OneClickListener : View.OnClickListener {
    override fun onClick(v: View) {
        if (started || SystemClock.elapsedRealtime() - lastClickEndTime < 500) {
            //Log.d(OneClickListener.class.toString(), "Rejected double click, " + new Date().toString() );
            return
        }
        //Log.d(OneClickListener.class.toString(), "One click, start: " + new Date().toString() );
        try {
            started = true
            onOneClick(v)
        } finally {
            started = false
            lastClickEndTime = SystemClock.elapsedRealtime()
            //Log.d(OneClickListener.class.toString(), "One click, end: " + new Date().toString() );
        }
    }

    protected abstract fun onOneClick(v: View?)

    companion object {
        private var started = false
        private var lastClickEndTime: Long = 0
    }
}