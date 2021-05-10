package com.uos.smsmsm.recycleradapter.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.uos.smsmsm.base.BaseAdapter
import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemCommentThumbnailBinding

class CommentAdapter : BaseAdapter<ContentDTO.Comment>(CommentDiffCallback()) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<out ViewDataBinding, ContentDTO.Comment> {
        val binding = ItemCommentThumbnailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentHolder(binding = binding)
    }

    override fun onBindViewHolder(
        holder: BaseHolder<out ViewDataBinding, ContentDTO.Comment>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).timestamp!!.toLong()
    }

    private class CommentDiffCallback : DiffUtil.ItemCallback<ContentDTO.Comment> () {
        override fun areItemsTheSame(
            oldItem: ContentDTO.Comment,
            newItem: ContentDTO.Comment
        ): Boolean {
            return oldItem.timestamp!! == newItem.timestamp!!
        }

        override fun areContentsTheSame(
            oldItem: ContentDTO.Comment,
            newItem: ContentDTO.Comment
        ): Boolean {
            return oldItem.timestamp!! == newItem.timestamp!!
        }


    }
}