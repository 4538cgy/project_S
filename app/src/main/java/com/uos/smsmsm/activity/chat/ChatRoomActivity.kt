package com.uos.smsmsm.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
=======
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityChatroomBinding

class ChatRoomActivity : AppCompatActivity() {

    private val model : ChatRoomViewModel by viewModels()
    private lateinit var binding : ActivityChatroomBinding
>>>>>>> parent of 8559208 (코드가 너저분하긴한데 날아갈까 걱정되어 commit...)

class ChatRoomActivity {

    inner class OneToOneChatRoomActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
<<<<<<< HEAD
=======
            binding = DataBindingUtil.setContentView(this, R.layout.activity_chatroom)
            binding.lifecycleOwner = this
            binding.chatroomviewmodel = model


>>>>>>> parent of 8559208 (코드가 너저분하긴한데 날아갈까 걱정되어 commit...)
        }
    }

    inner class GroupChatRoomActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }

}