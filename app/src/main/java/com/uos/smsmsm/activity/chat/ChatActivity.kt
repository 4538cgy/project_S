package com.uos.smsmsm.activity.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityChatBinding
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    lateinit var binding : ActivityChatBinding
    private val viewmodel : SNSUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel



        setSupportActionBar(binding.activityChatToolbar)
        actionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_option_bar_search_more,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_search -> {
                true
            }
            R.id.action_another -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}