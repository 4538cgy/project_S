package com.uos.smsmsm.activity.comment

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.signup.SignUpWithPhoneActivity
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityCommentBinding
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentActivity : BaseActivity<ActivityCommentBinding>(R.layout.activity_comment) {

    private val contentViewModel : ContentUtilViewModel by viewModels()
    private val snsViewModel : SNSUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            contentviewmodel = contentViewModel
            snsviewmodel = snsViewModel
            activitycomment = this@CommentActivity
            activityCommentImagebuttonClose.setOnClickListener { finish() }
            activityCommentButtonUpload.setOnClickListener {
                //댓글 올리기
            }
            //프로필 이미지 연결
            //리사이클러뷰 연결
            activityCommentRecycler.adapter
            activityCommentRecycler.layoutManager

            //댓글 작성자의 프로필 가져오기
            mainScope.launch {
                userRepository.getUserProfileImage(auth.currentUser!!.uid).collect{

                    Glide.with(root.context).load(it).apply(RequestOptions().circleCrop()).into(activityCommentImageviewMyProfile)
                }
            }
        }


    }
}