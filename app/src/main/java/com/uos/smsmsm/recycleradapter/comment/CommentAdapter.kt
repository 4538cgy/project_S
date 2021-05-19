package com.uos.smsmsm.recycleradapter.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.uos.smsmsm.base.BaseAdapter
import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.databinding.ItemCommentThumbnailBinding
import com.uos.smsmsm.recycleradapter.diffCallback.CommentDiffCallback

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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = currentList.size


}