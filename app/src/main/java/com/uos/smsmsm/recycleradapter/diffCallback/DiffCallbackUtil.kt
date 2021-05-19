package com.uos.smsmsm.recycleradapter.diffCallback

import androidx.recyclerview.widget.DiffUtil
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.RecyclerDefaultModel

class FriendsListDiffCallback : DiffUtil.ItemCallback<RecyclerDefaultModel> () {
    override fun areItemsTheSame(
        oldItem: RecyclerDefaultModel,
        newItem: RecyclerDefaultModel
    ): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(
        oldItem: RecyclerDefaultModel,
        newItem: RecyclerDefaultModel
    ): Boolean {
        return oldItem == newItem
    }
}
class CommentDiffCallback : DiffUtil.ItemCallback<ContentDTO.Comment> () {
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
class ReplyCommentDiffCallback : DiffUtil.ItemCallback<ContentDTO.Comment.ReplyComment> () {
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