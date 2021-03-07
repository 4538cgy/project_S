package com.uos.smsmsm.util.extensions

import android.app.Activity
import android.widget.Toast

/**
 * Created by SungBin on 3/7/21.
 */

// toast extension 추가
fun Activity.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(applicationContext, message, duration).show()
}
