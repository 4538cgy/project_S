package com.uos.smsmsm.activity.chat

import androidx.lifecycle.ViewModel
import com.uos.smsmsm.data.ChatDTO


class ChatRoomViewModel(private val repository: ChatListRepository) : ViewModel() {

    fun getChatRoomList(data : ChatDTO) = repository.getChatRoomListData()

    fun getChatRoomInfo(){


    }
}


