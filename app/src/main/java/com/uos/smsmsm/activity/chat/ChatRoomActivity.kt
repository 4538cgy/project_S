package com.uos.smsmsm.activity.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.safetynet.VerifyAppsConstants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.uos.smsmsm.data.ChatDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomActivity {

    val database = FirebaseDatabase.getInstance()

    inner class OneToOneChatRoomActivity : AppCompatActivity(){



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO){
                    }
                }
        }
    }

    inner class GroupChatRoomActivity : AppCompatActivity(){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }

}

