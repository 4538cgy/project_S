package com.uos.smsmsm.recycleradapter

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uos.smsmsm.recycleradapter.friends.FindFriendAdapter
import com.uos.smsmsm.data.UserDTO
import java.util.*


//Adapter들 관련
object BindingAdapter  {

    @JvmStatic
    @BindingAdapter("loadSrc")
    fun loadSrc(imageView: ImageView, @DrawableRes res: Int) {
        Glide.with(imageView.context).load(res).into(imageView)
    }
    // 날짜를 통하여 오늘, 이번주 ,이번달 표시를 해주는 fun 안에 로직 작성 필요
    @JvmStatic
    @BindingAdapter("headerText")
    fun headerText(textView: TextView, date: Date){
        textView.text = "오늘"
    }
    @JvmStatic
    @BindingAdapter("bind:findfrienditem")
    fun bindItem(recyclerView: RecyclerView, userDTOList: ObservableArrayList<UserDTO>) {
        val adapter: FindFriendAdapter = recyclerView.adapter as FindFriendAdapter
        adapter.setItem(userDTOList)
    }

}