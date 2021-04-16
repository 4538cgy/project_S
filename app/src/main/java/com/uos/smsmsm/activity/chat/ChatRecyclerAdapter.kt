package com.uos.smsmsm.activity.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.databinding.ItemChatBubbleBinding
import com.uos.smsmsm.util.time.TimeUtil

class ChatRecyclerAdapter(private var context: Context, private val list : LiveData<ArrayList<ChatDTO.Comment>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemChatBubbleBinding.inflate(LayoutInflater.from(context),parent,false)

        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int = list.value!!.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageViewHolder).onBind(list.value!![position])
        if (list.value!![position].uid.equals(uid)){
            holder.binding.messageItemTextViewMessage.text = list.value!![position].message
            holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.background_round_white)
            holder.binding.messageItemLinearlayoutDestination.visibility = View.INVISIBLE
            holder.binding.messageItemLinearlayoutMain.gravity = Gravity.RIGHT
        }else{
            //프로필 이미지 가져오고

            //유저 닉네임 가져오고

            holder.binding.messageItemLinearlayoutDestination.visibility = View.VISIBLE
            holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.background_round_gray)
            holder.binding.messageItemTextViewMessage.text = list.value!![position].message
            holder.binding.messageItemTextViewMessage.textSize = 18F
            holder.binding.messageItemLinearlayoutMain.gravity = Gravity.LEFT
        }
        holder.binding.messageItemTextviewTimestamp.text = TimeUtil().getTime()
    }

    inner class MessageViewHolder(var binding : ItemChatBubbleBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ChatDTO.Comment){
            binding.itemchatbubble = data
        }
    }
}