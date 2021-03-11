package com.uos.smsmsm.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// 채팅 / Timeline / 친구 찾기등 소셜 네트워크 기능 viewmodel
class SNSUtilViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(){

    var recyclerData : MutableLiveData<ArrayList<RecyclerDefaultModel>> = MutableLiveData()
    var edittextText : MutableLiveData<String> = MutableLiveData()
    var chatRoomUid : MutableLiveData<String> = MutableLiveData()
    var chatList : MutableLiveData<ArrayList<ChatDTO.Comment>> = MutableLiveData()

    //에딧 텍스트 TextWatcher
    fun textWatcher() = object :TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            edittextText.postValue(s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            edittextText.postValue(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            edittextText.postValue(s.toString())
        }

    }
    
    //채팅방 있는지 확인하고 있으면 snapshot id 가져오기
    fun checkChatRoom(destinationUid : String){
        val repository = ChatRepository()
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkChatRoom(destinationUid).collect {
                chatRoomUid.postValue(it)
            }
        }
    }

    //메세지 가져오기
    fun getMessageList(){
        val repository = ChatRepository()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getChat(chatRoomUid.value.toString()).collect {
                chatList.postValue(it)
            }
        }
    }

    //채팅 보내기
    /*
    Q: 뷰에서 sendMessage를 onClick으로 호출할 때 아래와 같은 인자값들을 어떻게 전달합니까?
    Sub Q: 혹시 이 인자들을 호출시에 전달하지않고 ChatRoomFragment 혹은 메세지를 전달해야하는 View에서 라이브데이터에 이 인자들의 값을 넣어두고 꺼내올 수 있습니까?
     */
    fun sendMessage(view: View,destinationUid: String,chatDTO: ChatDTO,comment: ChatDTO.Comment){
        val repository = ChatRepository()
        /*
        println("${edittextText.value.toString()} 가 전송되었습니다.")
         */
        viewModelScope.launch(Dispatchers.IO) {
            //채팅방 id가 없다면 채팅방 생성 후 메세지 전달
            if (chatRoomUid == null) {
                repository.createChatRoom(destinationUid,chatDTO).collect {

                }
            //채팅방이 있다면 그냥 메세지 전달
            }else{
                repository.addChat(chatRoomUid.value.toString(),comment).collect {

                }
            }
        }

        //채팅 다 보낸뒤 edittextText 교체해주기
        edittextText.postValue(null)


    }


    //채팅방 목록 가져오기
    //채팅방 목록 최초 초기화
    //호출 후 recyclerView 반드시 notifyChange
    fun initChatRoomList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2045%EC%B4%88_.png?alt=media&token=b15104e7-ed95-4acd-8f41-c9890af2a5ec",
                null,
                "아 적기 힘들다",
                "아 뭐적어야함"
            )
        )

        recyclerData.postValue(list)
    }

    //채팅 목록 가져오기
    //채팅 목록 최초 초기화
    //호출 후 recyclerView 반드시 notifyChange
    fun initChatList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        getMessageList()
        recyclerData.postValue(list)
    }



    //친구 목록 가져오기
    //친구 목록 최초 초기화
    //호출 후 recyclerView 반드시 notifyChange
    fun initFriendsList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.TEXT_TYPE,
                "",
                null,
                "즐겨찾기",
                ""
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2045%EC%B4%88_.png?alt=media&token=b15104e7-ed95-4acd-8f41-c9890af2a5ec",
                null,
                "아 적기 힘들다",
                "아 뭐적어야함."
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2034%EC%B4%88_.png?alt=media&token=7de3d26f-59ce-44da-a588-a0e4728140c5",
                null,
                "24세빻돔",
                "뭘봐 시벌"
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
                null,
                "FD_양꼬치와칭따오",
                "양꼬치 같이드실분"
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
                null,
                "퀸더블랙",
                ""
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.TEXT_TYPE,
                "",
                null,
                "친구 62",
                ""
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2000%EC%B4%88_.png?alt=media&token=32d9aa22-3b5b-4d2d-a3da-dc65b783d576",
                null,
                "FS_로뎅",
                "22살 서울시 부산구 동대문동 서식중"
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2007%EC%B4%88_.png?alt=media&token=f551c7b1-04dc-4ffb-a0d1-b80509ec153a",
                null,
                "MS 24/7",
                "캬ㅕ캬컄캬캬컄ㅋ"
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2052%EB%B6%84%2004%EC%B4%88_.png?alt=media&token=fd6e1bb5-b45a-4528-a292-aa48189538dd",
                null,
                "내다릴봐예뿌짜낭",
                ""
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2021%EC%B4%88_.png?alt=media&token=5cf7b0b8-7920-4394-a047-635c15e417e5",
                null,
                "으컁컁",
                "으캬캬캬컄ㅇ컁컁ㅋㅇ"
            )
        )
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2026%EC%B4%88_.png?alt=media&token=bcc4d7dc-b58e-43e3-8b96-08b14f20f2ea",
                null,
                "노예구함",
                "이쁜이구해용~"
            )
        )
        recyclerData.postValue(list)
    }

}