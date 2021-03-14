package com.uos.smsmsm.activity.setting

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object SettingBinding {

    @JvmStatic
    @BindingAdapter("loadSrc")
    fun loadSrc(imageView: ImageView, @DrawableRes res: Int) {
        Glide.with(imageView.context).load(res).into(imageView)
    }
}
