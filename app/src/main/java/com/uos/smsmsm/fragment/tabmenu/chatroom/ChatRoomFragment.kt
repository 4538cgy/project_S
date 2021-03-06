package com.uos.smsmsm.fragment.tabmenu.chatroom

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatRoomActivity
import com.uos.smsmsm.databinding.FragmentChatRoomBinding


class ChatRoomFragment : Fragment() {

    lateinit var binding : FragmentChatRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat_room,container,false)

            startActivity(Intent(binding.root.context,ChatRoomActivity::class.java))

        return binding.root
    }


}