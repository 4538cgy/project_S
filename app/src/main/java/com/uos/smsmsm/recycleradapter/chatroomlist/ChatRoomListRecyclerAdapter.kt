package com.uos.smsmsm.recycleradapter.chatroomlist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.databinding.ItemChatRoomListBinding
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.util.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ChatRoomListRecyclerAdapter(private val context : Context, private val list : LiveData<ArrayList<ChatDTO>>) : RecyclerView.Adapter<ChatRoomListRecyclerAdapter.ChatListViewHolder>() {

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val userRepository = UserRepository()

    inner class ChatListViewHolder(var binding: ItemChatRoomListBinding) : RecyclerView.ViewHolder(
        binding.root) {
        fun onBind(data: ChatDTO) {
            binding.listchatroom = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding = ItemChatRoomListBinding.inflate(LayoutInflater.from(context),parent,false)

        return ChatListViewHolder(binding)
    }

    override fun getItemCount(): Int = list.value!!.size

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.onBind(list.value!![position])

        //list[posion에 있는 유저들 (1:1 일경우 상대방만)]
        var destinationUsers : ArrayList<String> = arrayListOf()

        //일일 챗방에 있는 유저를 체크
        for(user in list.value!![position].users.keys){
            if (user != uid || list.value!![position].chatType!="personal"){
                destinationUsers.add(user)
            }
        }

        //프로필 이미지
        mainScope.launch {
            userRepository.getUserProfileImage(destinationUsers[0]).collect {
                Glide.with(holder.itemView.context).load(it).apply(RequestOptions().circleCrop()).into(holder.binding.itemChatRoomListCircleimageview)
            }
        }


        //오픈챗이 아닐경우 채팅방이름 유저명으로 설정
        if(list.value!![position].chatType!="open") {
            mainScope.launch {
                userRepository.getUserNickName(destinationUsers[0]).collect {
                    holder.binding.itemChatRoomListTextviewTitle.text = it
                }
            }
        }

//        //메세지를 내림차순으로 정렬 후 마지막 메세지의 키값을 가져옴
//        val commentMap : MutableMap<String,ChatDTO.Comment> = TreeMap(Collections.reverseOrder())
//        commentMap.putAll(list.value!![position].comments)
//        val lastMessageKey = commentMap.keys.toTypedArray()[0]
//        holder.binding.itemChatRoomListTextviewLastMessage.text = list.value!![position].comments[lastMessageKey]?.message

        //아이템 클릭시 채팅방으로 이동
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.apply {
                putExtra("chatUid",list.value!![position].chatuid)
                putExtra("chatType", list.value!![position].chatType)
                putExtra("chatTitle", list.value!![position].chatTitle)
                context.startActivity(intent)
            }
        }

        //시간 표시
//        holder.binding.itemChatRoomListTextviewTimestamp.text = TimeUtil().formatTimeString(list.value!![position].comments[lastMessageKey]?.timestamp!!.toLong())
    }
}