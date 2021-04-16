package com.uos.smsmsm.activity.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ActivityChatBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    lateinit var binding : ActivityChatBinding
    private val viewmodel : SNSUtilViewModel by viewModels()

    var destinationUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel

        destinationUid = intent.getStringExtra("destinationUid")
        //액션바 Toolbar에 바인딩
        setSupportActionBar(binding.activityChatToolbar)
        //액션바 제목 지우기
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //채팅 보내기
        binding.activityChatImagebuttonSendmessage.setOnClickListener { viewmodel.sendMessage(destinationUid) }







        viewmodel.checkChatRoom(destinationUid)

        viewmodel.chatRoomUid.observe(this, Observer {
            var data = MutableLiveData<ArrayList<ChatDTO.Comment>>()
            viewmodel.chatList.observe(this, Observer {
                    livedata ->
                data.value = livedata
                binding.activityChatRecyclerview.adapter = ChatRecyclerAdapter(binding.root.context,data,destinationUid)
                binding.activityChatRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,
                    LinearLayoutManager.VERTICAL,false)
            })
        })



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_option_bar_search_more,menu)
        return true
    }

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