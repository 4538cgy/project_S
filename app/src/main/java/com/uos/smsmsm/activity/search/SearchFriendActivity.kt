package com.uos.smsmsm.activity.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivitySearchFriendBinding
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

// Apply ktlint
@AndroidEntryPoint
class SearchFriendActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchFriendBinding
    private val viewmodel : SNSUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_friend)
        binding.searchfriend = this
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel


    }

    //뒤로가기
    fun onBack(view: View){
        finish()
    }


}
