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

    fun initFriendsList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.TEXT_TYPE,
            "",
            null,
            "즐겨찾기",
            ""
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2045%EC%B4%88_.png?alt=media&token=b15104e7-ed95-4acd-8f41-c9890af2a5ec",
            null,
            "아 적기 힘들다",
            "아 뭐적어야함"
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2034%EC%B4%88_.png?alt=media&token=7de3d26f-59ce-44da-a588-a0e4728140c5",
            null,
            "24세빻돔",
            "뭘봐 시벌"
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
            null,
            "FD_양꼬치와칭따오",
            "양꼬치 같이드실분"
        ))
        list.add( RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
            null,
            "퀸더블랙",
            ""
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.TEXT_TYPE,
            "",
            null,
            "친구 62",
            ""
        ))
        list.add( RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2000%EC%B4%88_.png?alt=media&token=32d9aa22-3b5b-4d2d-a3da-dc65b783d576",
            null,
            "FS_로뎅",
            "22살 서울시 부산구 동대문동 서식중"
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2007%EC%B4%88_.png?alt=media&token=f551c7b1-04dc-4ffb-a0d1-b80509ec153a",
            null,
            "MS 24/7",
            "캬ㅕ캬컄캬캬컄ㅋ"
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2052%EB%B6%84%2004%EC%B4%88_.png?alt=media&token=fd6e1bb5-b45a-4528-a292-aa48189538dd",
            null,
            "내다릴봐예뿌짜낭",
            ""
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2021%EC%B4%88_.png?alt=media&token=5cf7b0b8-7920-4394-a047-635c15e417e5",
            null,
            "으컁컁",
            "으캬캬캬컄ㅇ컁컁ㅋㅇ"
        ))
        list.add(RecyclerDefaultModel(
            RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
            "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2026%EC%B4%88_.png?alt=media&token=bcc4d7dc-b58e-43e3-8b96-08b14f20f2ea",
            null,
            "노예구함",
            "이쁜이구해용~"
        ))
        recyclerData.postValue(list)
    }

}