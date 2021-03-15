package com.uos.smsmsm.util.extensions

import android.view.View

/**
 * Created by SungBin on 3/14/21.
 */

fun View.hide(isGone: Boolean = false) {
    visibility = if (isGone) View.GONE else View.INVISIBLE
}
