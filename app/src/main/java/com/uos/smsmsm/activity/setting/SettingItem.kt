package com.uos.smsmsm.activity.setting

import androidx.annotation.DrawableRes

/**
 * Created by SungBin on 3/14/21.
 */

data class SettingItem(
    val type: SettingItemType,
    @DrawableRes val icon: Int? = null,
    val name: String? = null,
    val hideLine: Boolean = false
)
