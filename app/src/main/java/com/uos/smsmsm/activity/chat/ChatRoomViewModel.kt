package com.uos.smsmsm.activity.chat

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.uos.smsmsm.data.ChatDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomViewModel(private val repository: ChatListRepository) : ViewModel() {

    fun getChatRoomList(data : ChatDTO) = repository.getChatRoomListData()

    fun getChatRoomInfo(){


    }
}