package com.uos.smsmsm.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomActivity {
    @Inject
    lateinit var chatroomListRecyclerAdapter: ChatRoomListRecyclerAdapter
    inner class OneToOneChatRoomActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }

    inner class GroupChatRoomActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }

}