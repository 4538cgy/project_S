package com.uos.smsmsm.fragment.tabmenu.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentChatRoomBinding

class ChatRoomFragment : Fragment() {

    lateinit var binding: FragmentChatRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false)

        return binding.root
    }
}
