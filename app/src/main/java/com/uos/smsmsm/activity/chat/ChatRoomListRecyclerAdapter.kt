package com.uos.smsmsm.activity.chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uos.smsmsm.data.ChatDTO
import java.util.ArrayList

class ChatRoomListRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var chat : ArrayList<ChatDTO> = arrayListOf()
    var destinationUsers : ArrayList<String> = arrayListOf()                                                        //채팅을 받는 유저의 uid
    var uid : String ? = null                                                                                       //채팅을 보내는 유저의 uid

    init {
        FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid")
            .equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    chat.clear()
                    for (item in dataSnapshot.children) {
                        chat.add(item.getValue(ChatDTO::class.java)!!)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        uid = FirebaseAuth.getInstance().uid.toString()
        // 챗룸 데이터 가져오기



    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = chat.size

    class ChatRoomListOneToOneViewHolder(){

    }

    class ChatRoomListGroupViewHoler(){

    }
}