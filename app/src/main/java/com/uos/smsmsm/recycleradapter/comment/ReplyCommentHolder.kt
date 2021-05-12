package com.uos.smsmsm.recycleradapter.comment

import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.databinding.ItemCommentReplyThumbnailBinding
import com.uos.smsmsm.databinding.ItemCommentThumbnailBinding
import com.uos.smsmsm.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReplyCommentHolder(binding : ItemCommentReplyThumbnailBinding) : BaseHolder<ItemCommentReplyThumbnailBinding, ContentDTO.Comment.ReplyComment>(binding) {
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val userRepository = UserRepository()

    override fun bind(element: ContentDTO.Comment.ReplyComment) {
        super.bind(element)
        binding.itemcommentreply = element

    }
}