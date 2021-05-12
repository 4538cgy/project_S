package com.uos.smsmsm.activity.comment

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.signup.SignUpWithPhoneActivity
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ActivityCommentBinding
import com.uos.smsmsm.recycleradapter.comment.CommentAdapter
import com.uos.smsmsm.recycleradapter.timeline.TimeLineAdapter
import com.uos.smsmsm.repository.ContentRepository
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
    private val contentRepository = ContentRepository()


    private val adapter by lazy { CommentAdapter().apply {
        println("list 전달 ${list.toString()}")
        submitList(list)
    } }

    private val list by lazy { ArrayList<ContentDTO.Comment>().apply {
        println("리스트 생성")
    }}





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            contentviewmodel = contentViewModel
            snsviewmodel = snsViewModel
            activitycomment = this@CommentActivity


            activityCommentImagebuttonClose.setOnClickListener { finish() }
            activityCommentButtonUpload.setOnClickListener {
                //댓글 올리기
                var comments = ContentDTO.Comment()
                println("으으으으으으 ${snsViewModel.edittextText.value}")
                comments.comment = snsViewModel.edittextText.value.toString()
                comments.timestamp = System.currentTimeMillis()
                comments.uid = auth.currentUser!!.uid

                contentViewModel.uploadComment(comments,intent.getStringExtra("postId"))
            }
            //프로필 이미지 연결
            //리사이클러뷰 연결
            mainScope.launch {
                contentRepository.getContents(intent.getStringExtra("postId")).collect {
                    it.forEach { contentMap ->
                        if (contentMap != null){
                            contentMap.value.commentList.forEach { commentMap ->
                                if (commentMap != null){
                                    list.add(commentMap.value)
                                }
                            }
                        }
                    }
                    binding.activityCommentRecycler.adapter = adapter
                    binding.activityCommentRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
                    adapter.submitList(list)
                    adapter.notifyItemInserted(list.lastIndex)
                }
            }

            //댓글 작성자의 프로필 가져오기
            mainScope.launch {
                userRepository.getUserProfileImage(auth.currentUser!!.uid).collect{

                    Glide.with(root.context).load(it).apply(RequestOptions().circleCrop()).into(activityCommentImageviewMyProfile)
                }
            }
        }


    }
}