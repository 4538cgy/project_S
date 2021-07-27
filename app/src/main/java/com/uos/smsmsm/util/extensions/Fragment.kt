package com.uos.smsmsm.util.extensions

import androidx.fragment.app.Fragment
import com.uos.smsmsm.util.AutoClearedValue

fun <T : Any> Fragment.viewBinding() = AutoClearedValue<T>(this)