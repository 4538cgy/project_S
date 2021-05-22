package com.uos.smsmsm.fragment.tabmenu.chatroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.AddOpenChatActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.databinding.FragmentChatRoomBinding
import com.uos.smsmsm.recycleradapter.chatroomlist.ChatRoomListRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatRoomFragment : BaseFragment<FragmentChatRoomBinding>( R.layout.fragment_chat_room) {

    private val viewmodel: SNSUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentchatroom = this
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).setSupportActionBar(binding.fragmentChatRoomToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

        loadingDialog.show()
        viewmodel.initMyChatRoomList(auth.currentUser!!.uid)
        initRecyclerView()
    }

    //리사이클러뷰 초기화 함수
    fun initRecyclerView(){
        var data = MutableLiveData<ArrayList<ChatDTO>>()
        val recyclerObserver : Observer<ArrayList<ChatDTO>>
        = Observer { livedata -> data.value = livedata
            println(" 프래그먼트에 들어갈 리스트 : " + livedata)
            binding.fragmentChatRoomRecyclerview.adapter = ChatRoomListRecyclerAdapter(binding.root.context, data)
            binding.fragmentChatRoomRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
            loadingDialog.dismiss()
        }

        viewmodel.chatRoomList.observe(viewLifecycleOwner, recyclerObserver)
    }

    //상단 툴바 생성함수
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_option_bar,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->{
                return true
            }
            R.id.action_add -> {
                startActivityForResult(Intent(context, AddOpenChatActivity::class.java),1721)
                return true
            }
            R.id.action_another ->{
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
