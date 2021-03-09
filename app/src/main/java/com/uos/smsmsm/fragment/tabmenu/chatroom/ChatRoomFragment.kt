package com.uos.smsmsm.fragment.tabmenu.chatroom

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
        binding.fragmentchatroom = this
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.fragmentChatRoomToolbar)
        var actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

        return binding.root
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
