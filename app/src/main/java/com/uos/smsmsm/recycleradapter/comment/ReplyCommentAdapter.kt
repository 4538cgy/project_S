package com.uos.smsmsm.recycleradapter.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.uos.smsmsm.base.BaseAdapter
import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.databinding.ItemCommentReplyThumbnailBinding
import com.uos.smsmsm.databinding.ItemCommentThumbnailBinding

class ReplyCommentAdapter : BaseAdapter<ContentDTO.Comment.ReplyComment>(ReplyCommentDiffCallback()) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<out ViewDataBinding, ContentDTO.Comment.ReplyComment> {
        val binding = ItemCommentReplyThumbnailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReplyCommentHolder(binding = binding)
    }

    override fun onBindViewHolder(
        holder: BaseHolder<out ViewDataBinding, ContentDTO.Comment.ReplyComment>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).timestamp!!.toLong()
    }


    private class ReplyCommentDiffCallback : DiffUtil.ItemCallback<ContentDTO.Comment.ReplyComment> () {
        override fun areItemsTheSame(
            oldItem: ContentDTO.Comment.ReplyComment,
            newItem: ContentDTO.Comment.ReplyComment
        ): Boolean {
            return oldItem.timestamp!! == newItem.timestamp!!
        }

        override fun areContentsTheSame(
            oldItem: ContentDTO.Comment.ReplyComment,
            newItem: ContentDTO.Comment.ReplyComment
        ): Boolean {
            return oldItem.timestamp!! == newItem.timestamp!!
        }


    }
}