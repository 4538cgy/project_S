package com.uos.smsmsm.recycleradapter.timeline

import android.content.Context
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.comment.CommentActivity
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemTimelinePostBinding
import com.uos.smsmsm.recycleradapter.viewpager.PhotoAdapter
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.util.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimeLineHolder(binding: ItemTimelinePostBinding) : BaseHolder<ItemTimelinePostBinding, TimeLineDTO>(binding) {


    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val contentRepository = ContentRepository()
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    override fun bind(element: TimeLineDTO) {
        super.bind(element)
        binding.itemtimelinepost = element
        //글쓴 유저의 uid를 가져온뒤 프로필 이미지 삽입
        mainScope.launch {
            userRepository.getUserProfileImage(element.content!!.uid.toString()).collect {

                Glide.with(binding.root.context).load(it)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.itemTimelinePostImageviewProfileImage)
            }
        }
        //글쓴 유저의 닉네임
        mainScope.launch {
            userRepository.getUserNickName(element.content!!.uid!!).collect {
                binding.itemTimelinePostTextviewNickname.text = it
            }
        }

        //viewpager에 사진 연결
        if (element.content!!.imageDownLoadUrlList != null) {
            if (element.content!!.imageDownLoadUrlList!!.size > 0) {
                binding.itemTimelinePostViewpagerPhotoList.visibility = View.VISIBLE
                binding.itemTimelinePostViewpagerPhotoList.adapter =
                    PhotoAdapter(
                        binding.root.context,
                        element.content!!.imageDownLoadUrlList!!
                    )

            } else {
                binding.itemTimelinePostViewpagerPhotoList.visibility = View.GONE

            }
        }
        //시간 표시
        binding.itemTimelinePostTextviewTimestamp.text =
            TimeUtil().formatTimeString(element.content!!.timestamp!!)

        //댓글창이 3개 이하면 댓글 리사이클러뷰 연결
        //보류
        if (element.content!!.commentCount!!.toInt() < 3) {
            binding.itemTimelinePostRecyclerviewFriendscomments.visibility = View.VISIBLE
            binding.itemTimelinePostRecyclerviewFriendscomments.adapter
        } else {
            binding.itemTimelinePostRecyclerviewFriendscomments.visibility = View.GONE
        }

        //게시글이 친구면 댓글창 보여주기


        //댓글이 0개 이상이면 갯수 연결
        if (element.content!!.commentCount!!.toInt() > 0) {
            binding.itemTimelinePostTextviewCommentsCount.text =
                element.content!!.commentCount.toString()
        } else {
            binding.itemTimelinePostTextviewCommentsCount.visibility = View.GONE
        }

        //dm 액션
        binding.itemTimelineImagebuttonDirectMessage.setOnClickListener {
            var intentChat = Intent(binding.root.context, ChatActivity::class.java)
            intentChat.apply {
                putExtra("destinationUid", element.content!!.uid.toString())
                binding.root.context.startActivity(intentChat)
            }
        }
        //댓글 액션 = 댓글 activity로 이동 + 댓글 edittext 클릭시 이동 + 댓글 버튼 클릭시 이동
        binding.itemTimelineImagebuttonComments.setOnClickListener { comment() }
        binding.itemTimelinePostConstNoneComment.setOnClickListener { comment() }
        binding.itemTimelinePostButtonWriteComment.setOnClickListener { comment() }

        //댓글 작성하기 좌측에 보고있는 유저의 프로필 연결
        mainScope.launch {
            userRepository.getUserProfileImage(auth.currentUser!!.uid.toString()).collect {

                Glide.with(binding.root.context).load(it)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.itemTimelinePostImageviewCommentProfile)
            }
        }
        //옵션 버튼 클릭시 팝업 박스 표시

        //사진 더블 클릭시 좋아요 액션

        //프로필 이미지 클릭시 프로필로 이동
        binding.itemTimelinePostImageviewProfileImage.setOnClickListener {
            var intent = Intent(binding.root.context, ProfileActivity::class.java)
            intent.apply {
                putExtra("uid", element.content!!.uid)
            }
            binding.root.context.startActivity(intent)
        }

        //테스트용 글 내용 삽입
        binding.itemTimelinePostTextviewExplain.text = element.content!!.explain

        //테스트용 좋아요 클릭
        binding.itemTimelineImagebuttonFavorite.setOnClickListener {
            favoriteEvent( element.contentId.toString() )
        }

        isFavorite(element.contentId.toString())
    }

    fun comment() {
        var intentComment = Intent(context, CommentActivity::class.java)
        intentComment.apply {
            putExtra("postId", element.contentId.toString())
            context.startActivity(intentComment)
        }
    }

    fun favoriteEvent(contentId : String) {
        mainScope.launch {
            contentRepository.favoriteEvent(contentId).collect {
                if (it) isFavorite(contentId)
            }
        }
    }

    fun isFavorite(contentId : String) {
        mainScope.launch {
            //로딩 시작 넣어주기
            contentRepository.isFavorite(contentId).collect {
                if (it) {
                    //채워진 하트
                    binding.itemTimelineImagebuttonFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_36)
                    updateFavoriteCount(contentId)
                    //로딩 끝내기 넣어주기
                } else {
                    //빈 하트
                    binding.itemTimelineImagebuttonFavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_border_36)
                    updateFavoriteCount(contentId)
                    //로딩 끝내기 넣어주기
                }
            }
        }
    }

    //좋아요 갯수 업데이트
    fun updateFavoriteCount(contentId : String) {
        mainScope.launch {
            //로딩 시작 넣어주기
            contentRepository.getFavoriteCountByContentId(contentId)
                .collect {
                    binding.itemTimelinePostTextviewFavoriteCount.text =
                        "좋아요 " + it.toString() + "개"
                    //로딩 끝내기 넣어주기
                }
        }

    }
}