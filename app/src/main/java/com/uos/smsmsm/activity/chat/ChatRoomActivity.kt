package com.uos.smsmsm.activity.chat

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityChatroomBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ChatRoomActivity : AppCompatActivity() {

    private val model : ChatRoomViewModel by viewModels()
    private lateinit var binding : ActivityChatroomBinding


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this, R.layout.activity_chatroom)
            binding.lifecycleOwner = this
            binding.chatroomviewmodel = model

        }




}

