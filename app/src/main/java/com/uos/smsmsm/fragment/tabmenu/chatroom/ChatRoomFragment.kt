package com.uos.smsmsm.fragment.tabmenu.chatroom

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentChatRoomBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatRoomFragment : Fragment() {

    lateinit var binding: FragmentChatRoomBinding
    private val viewmodel: SNSUtilViewModel by viewModels()

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

        viewmodel.initChatRoomList()
        initRecyclerViewAdapter()

        return binding.root
    }

    fun initRecyclerViewAdapter(){
        var data = MutableLiveData<ArrayList<RecyclerDefaultModel>>()

        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
            = Observer { livedata ->
            data.value = livedata
            binding.fragmentChatRoomRecyclerview.adapter = MultiViewTypeRecyclerAdapter(binding.root.context,data)
            binding.fragmentChatRoomRecyclerview.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        }

        viewmodel.recyclerData.observe(viewLifecycleOwner, recyclerObserver)

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
