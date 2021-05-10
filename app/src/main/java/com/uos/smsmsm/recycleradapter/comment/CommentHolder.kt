package com.uos.smsmsm.recycleradapter.comment

import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.activity.comment.CommentActivity
import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.databinding.ItemCommentThumbnailBinding
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.util.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CommentHolder(binding: ItemCommentThumbnailBinding) : BaseHolder<ItemCommentThumbnailBinding,ContentDTO.Comment>(binding) {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val userRepository = UserRepository()

    override fun bind(element: ContentDTO.Comment) {
        super.bind(element)
        binding.itemcomment = element

        //댓글 내용
        binding.itemCommentThumbnailTextviewExplain.text = element.comment
        //닉네임
        mainScope.launch {
            userRepository.getUserNickName(element.uid!!).collect {
                binding.itemCommentThumbnailTextviewNickname.text = it
            }
        }
        //시간
        binding.itemCommentThumbnailTextviewTimestamp.text = TimeUtil().formatTimeString(element.timestamp!!.toLong())

        //프로필 이미지
        mainScope.launch {
            userRepository.getUserProfileImage(element.uid.toString()).collect {

                Glide.with(binding.root.context).load(it)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.itemCommentThumbnailImageviewProfileImage)
            }
        }

        //답글 달기
        itemView.setOnClickListener { setReply() }
    }



    //답글
    fun setReply(){

    }



}