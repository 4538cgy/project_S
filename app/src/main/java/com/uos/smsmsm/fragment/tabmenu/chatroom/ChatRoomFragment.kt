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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatRecyclerAdapter
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentChatRoomBinding
import com.uos.smsmsm.recycleradapter.ChatRoomListRecyclerAdapter
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.util.dialog.LoadingDialog
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatRoomFragment : Fragment() {

    lateinit var binding: FragmentChatRoomBinding
    private val viewmodel: SNSUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false)
        binding.fragmentchatroom = this
        setHasOptionsMenu(true)

        //프로그레스 초기화
        loadingDialog = LoadingDialog(binding.root.context)
        //프로그레스 투명하게
        loadingDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        loadingDialog!!.setCancelable(false)


        (activity as AppCompatActivity).setSupportActionBar(binding.fragmentChatRoomToolbar)
        var actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

        loadingDialog.show()
        viewmodel.initMyChatRoomList(auth.currentUser!!.uid)
        initRecyclerView()
        return binding.root
    }
    fun initRecyclerView(){
        var data = MutableLiveData<ArrayList<ChatDTO>>()
        val recyclerObserver : Observer<ArrayList<ChatDTO>>
            = Observer { livedata ->
            data.value = livedata
            binding.fragmentChatRoomRecyclerview.adapter = ChatRoomListRecyclerAdapter(binding.root.context,data)
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
