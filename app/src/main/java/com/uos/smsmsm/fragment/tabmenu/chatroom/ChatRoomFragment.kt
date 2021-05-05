package com.uos.smsmsm.fragment.tabmenu.chatroom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.databinding.FragmentChatRoomBinding
import com.uos.smsmsm.recycleradapter.chatroomlist.ChatRoomListRecyclerAdapter
import com.uos.smsmsm.util.dialog.LoadingDialog
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
    fun initRecyclerView(){
        var data = MutableLiveData<ArrayList<ChatDTO>>()
        val recyclerObserver : Observer<ArrayList<ChatDTO>>
            = Observer { livedata ->
            data.value = livedata
            binding.fragmentChatRoomRecyclerview.adapter =
                ChatRoomListRecyclerAdapter(
                    binding.root.context,
                    data
                )
            binding.fragmentChatRoomRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
            loadingDialog.dismiss()
        }

        viewmodel.chatRoomList.observe(viewLifecycleOwner, recyclerObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_option_bar,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->{
                return true
            }
            R.id.action_add-> {
                return true
            }
            R.id.action_another ->{
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
