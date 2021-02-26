package com.uos.smsmsm.activity.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivitySearchFriendBinding

class SearchFriendActivity : AppCompatActivity() {

    lateinit var binding : ActivitySearchFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_friend)
        binding.searchfriend = this@SearchFriendActivity
    }
}