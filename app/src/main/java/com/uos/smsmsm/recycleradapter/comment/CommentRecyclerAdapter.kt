package com.uos.smsmsm.recycleradapter.comment

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemCommentReplyThumbnailBinding
import com.uos.smsmsm.databinding.ItemCommentThumbnailBinding

class CommentRecyclerAdapter(private val context : Context , private val comments : ContentDTO.Comment) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class CommentViewHolder(val binding : ItemCommentThumbnailBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentDTO.Comment){
            binding.itemcomment = data
        }
    }

    inner class CommentReplyViewHolder(val binding : ItemCommentReplyThumbnailBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentDTO.Comment.ReplyComment){
            binding.itemcommentreply = data
        }
    }
}