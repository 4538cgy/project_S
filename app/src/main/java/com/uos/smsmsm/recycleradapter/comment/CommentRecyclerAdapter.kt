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

    //조되씀
    /*
    ContentDTO.Comment 에 ContentDTO.Comment.ReplyComment 가 물려있는 방식인데 이러면.. 리사이클러뷰 하나로 표현하는게 안됨.. 하슈발!
     */

    inner class CommentReplyViewHolder(val binding : ItemCommentReplyThumbnailBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : ContentDTO.Comment.ReplyComment){
            binding.itemcommentreply = data
        }
    }
}