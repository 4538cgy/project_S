package com.uos.smsmsm.activity.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.databinding.ActivityChatBinding
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class   ChatActivity : BaseActivity<ActivityChatBinding>(R.layout.activity_chat) {

    private val viewModel : SNSUtilViewModel by viewModels()

    var recyclerData : MutableLiveData<ArrayList<ChatDTO.Comment>> = MutableLiveData()
    //1:1 대상 uid
    var destinationUid : String? = ""
    //채팅방 uid
    var chatUid : String? = ""
    //채팅방 종류
    var chatType : String? = ""
    //오픈 채팅방 제목
    var chatTitle : String? = ""
    //리사이클러뷰 초기화 확인 플래그 (왜있는건지..?)
    var chatRecyclerAdapterInitChecker = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        destinationUid = intent.getStringExtra("destinationUid")
        chatTitle = intent.getStringExtra("chatTitle")

        println("destinationUid : " + chatUid + " chatType : " + chatType + " chatTitle : " +chatTitle)
        binding.apply {
            viewmodel = viewModel
            chat = this@ChatActivity
            //액션바 Toolbar에 바인딩
            setSupportActionBar(activityChatToolbar)
            //채팅 보내기
            activityChatImagebuttonSendmessage.setOnClickListener { viewModel.sendMessage(destinationUid,chatTitle) }
        }
        //액션바 제목 지우기
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //뷰모델 값 적용
        viewModel.apply {
            //destinationUid 값으로 채팅방이 있는지 찾아 뷰모델에 추가
            chatRoomUid.postValue(intent.getStringExtra("chatUid"))
            chatRoomType = intent.getStringExtra("chatType")
//            checkChatRoom(chatUid,chatType,chatTitle)
            //chatRoomUid 변화시 메세지 가져오기 -> chatList qusghktl 리사이클러뷰 다시 설정
            chatRoomUid.observe(this@ChatActivity, Observer { uid ->
                //채팅 데이터 가져오기 [ chatRoomUid ] 에 변화가 있다면
                println("chatRoomUid 변경 : " + chatRoomUid.value);
                getMessageList()
                chatList.observe(this@ChatActivity, Observer { livedata ->
                    println("chatList 변경 : " + chatList.value);
                    this@ChatActivity.recyclerData.value = livedata
                    if (!chatRecyclerAdapterInitChecker) {
                        initRecyclerAdapter()
                    } else {
                        binding.activityChatRecyclerview.adapter?.notifyDataSetChanged()
                        binding.activityChatRecyclerview.scrollToPosition(livedata.size - 1)
                    }
                })
            })
        }
    }

    fun backPressed(view : View){ finish() }

    //리사이클러뷰 초기화 함수
    fun initRecyclerAdapter(){
        chatRecyclerAdapterInitChecker = true
        with(binding) {
            activityChatRecyclerview.adapter = ChatRecyclerAdapter(rootContext, recyclerData)

            activityChatRecyclerview.layoutManager = LinearLayoutManager(rootContext,
                LinearLayoutManager.VERTICAL,false)
            activityChatRecyclerview.scrollToPosition(recyclerData.value!!.size - 1)
        }

    }

    //상단 툴바 생성함수
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_option_bar_search_more,menu)
        return true
    }

    //상단 툴바 선택시 실행되는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_search -> {
                true
            }
            R.id.action_another -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




}