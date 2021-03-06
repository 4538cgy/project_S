package com.uos.smsmsm.activity.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect

class ChatRoomViewModel( application: Application ) : AndroidViewModel(application) {
    val repository = ChatListRepository()

    fun getTest(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.Observe().collect {
                println("뷰모델에서의 데이터 출력 = " + it.toString())
            }
        }
    }

}


