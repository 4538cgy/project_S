package com.uos.smsmsm.activity.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.uos.smsmsm.data.ChatDTO


class ChatRoomViewModel( application: Application ) : AndroidViewModel(application) {
    val repository = ChatListRepository()

   fun getTest() {
       repository.userObserver()
   }

}


