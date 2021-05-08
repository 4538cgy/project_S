package com.uos.smsmsm.util

import android.content.Context
import android.util.TypedValue


fun dpToPx(context: Context, dp: Float) : Int {
    val dm = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm).toInt()

}