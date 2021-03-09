package com.uos.smsmsm.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.RecyclerDefaultModel

// 채팅 / Timeline / 친구 찾기등 소셜 네트워크 기능 viewmodel
class SNSUtilViewModel @ViewModelInject constructor( @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(){

    var recyclerData : MutableLiveData<ArrayList<RecyclerDefaultModel>> = MutableLiveData()

    fun initChatRoomList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        /* 데이터 가져와서 list에 꽂아넣기
        val list2 = listOf(
        RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
                null,
                "퀸더블랙",
                ""
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.TEXT_TYPE,
                "",
                null,
                "친구 62",
                ""
            )
         )
         */
        recyclerData.postValue(list)
    }

}