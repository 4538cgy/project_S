package com.uos.smsmsm.recycleradapter.timeline

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.comment.CommentActivity
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemTimelinePostBinding
import com.uos.smsmsm.recycleradapter.viewpager.PhotoAdapter
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.repository.UserRepository
import com.uos.smsmsm.repository.UtilRepository
import com.uos.smsmsm.util.time.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimeLineRecyclerAdapter(private val context : Context, private val list : LiveData<ArrayList<TimeLineDTO>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val userRepository = UserRepository()
    private val contentRepository = ContentRepository()
    private val utilRepository = UtilRepository()
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val auth  = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemTimelinePostBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return TimeLinePostViewHolder(binding = binding)
    }


    override fun getItemCount(): Int = list.value!!.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TimeLinePostViewHolder).onBind(list.value!![position])

        //글쓴 유저의 uid를 가져온뒤 프로필 이미지
        mainScope.launch {
            userRepository.getUserProfileImage(list.value!![position].content!!.uid!!).collect{

                Glide.with(holder.binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(holder.binding.itemTimelinePostImageviewProfileImage)
            }
        }
        //글쓴 유저의 닉네임
        mainScope.launch {
            userRepository.getUserNickName(list.value!![position].content!!.uid!!).collect {
                holder.binding.itemTimelinePostTextviewNickname.text = it
            }
        }
        //viewpager에 사진 연결
        if (list.value!![position].content!!.imageDownLoadUrlList != null) {
            if (list.value!![position].content!!.imageDownLoadUrlList!!.size > 0) {
                holder.binding.itemTimelinePostViewpagerPhotoList.visibility = View.VISIBLE
                holder.binding.itemTimelinePostViewpagerPhotoList.adapter =
                    PhotoAdapter(
                        holder.binding.root.context,
                        list.value!![position].content!!.imageDownLoadUrlList!!
                    )

            }else{
                holder.binding.itemTimelinePostViewpagerPhotoList.visibility = View.GONE

            }
        }
        //시간 표시
        holder.binding.itemTimelinePostTextviewTimestamp.text = TimeUtil().formatTimeString(list.value!![position].content!!.timestamp!!)

        //댓글창이 3개 이하면 댓글 리사이클러뷰 연결 
        //보류
        if (list.value!![position].content!!.commentCount!!.toInt() < 3){
            holder.binding.itemTimelinePostRecyclerviewFriendscomments.visibility = View.VISIBLE
            holder.binding.itemTimelinePostRecyclerviewFriendscomments.adapter
        }else{
            holder.binding.itemTimelinePostRecyclerviewFriendscomments.visibility = View.GONE
        }
        //게시글이 친구면 댓글창 보여주기


        //댓글이 0개 이상이면 갯수 연결
        if(list.value!![position].content!!.commentCount!!.toInt() > 0){
            holder.binding.itemTimelinePostTextviewCommentsCount.text = list.value!![position].content!!.commentCount.toString()
        }else{
            holder.binding.itemTimelinePostTextviewCommentsCount.visibility = View.GONE
        }
        //조회수 연결
        holder.binding.itemTimelinePostTextviewViewCounts.text = list.value!![position].content!!.viewCount.toString()
        //좋아요 액션
        holder.binding.itemTimelineImagebuttonFavorite.setOnClickListener { favoriteEvent(position) }

        //북마크 액션
        addBookMark()

        //dm 액션
        holder.binding.itemTimelineImagebuttonDirectMessage.setOnClickListener {
            var intentChat = Intent(holder.binding.root.context, ChatActivity::class.java)
            intentChat.apply {
                putExtra("destinationUid", list.value!![position].content!!.uid.toString())
                holder.binding.root.context.startActivity(intentChat)
            }
        }
        //댓글 액션 = 댓글 activity로 이동 + 댓글 edittext 클릭시 이동 + 댓글 버튼 클릭시 이동
        holder.binding.itemTimelineImagebuttonComments.setOnClickListener {comment(holder.binding.root.context, position)}
        holder.binding.itemTimelinePostConstNoneComment.setOnClickListener {comment(holder.binding.root.context, position)}
        holder.binding.itemTimelinePostButtonWriteComment.setOnClickListener {comment(holder.binding.root.context, position)}

        //게시글 내용
        holder.binding.itemTimelinePostTextviewExplain.text = list.value!![position].content!!.explain.toString()



        //이모티콘 액션 연결
        openEmoticonBar()
        //댓글 작성하기 좌측에 보고있는 유저의 프로필 연결
        mainScope.launch {
            userRepository.getUserProfileImage(auth.currentUser!!.uid.toString()).collect{

                Glide.with(holder.binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(holder.binding.itemTimelinePostImageviewCommentProfile)
            }
        }
        //옵션 버튼 클릭시 팝업 박스 표시
        openOption()
        //사진 더블 클릭시 좋아요 액션

        //프로필 이미지 클릭시 프로필로 이동
        holder.binding.itemTimelinePostImageviewProfileImage.setOnClickListener {
            var intent = Intent(holder.binding.root.context, ProfileActivity::class.java)
            intent.apply {
                putExtra("uid", list.value!![position].content!!.uid)
            }
            holder.binding.root.context.startActivity(intent)
        }
        
    }

    fun comment(context: Context , position: Int){
        var intentComment = Intent(context,CommentActivity::class.java)
        intentComment.apply {
            putExtra("postId",list.value!![position].contentId.toString())
            context.startActivity(intentComment)
        }
    }

    fun favoriteEvent(position: Int){
        mainScope.launch {
            contentRepository.favoriteEvent(list.value!![position].contentId!!).collect {
                println("이벤트 성공함? $it")
            }
        }
    }

    fun addBookMark(){

    }

    fun openOption(){

    }

    fun openEmoticonBar(){

    }

    inner class TimeLinePostViewHolder(val binding : ItemTimelinePostBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : TimeLineDTO){
            when(data.content!!.postState){
                //애드몹 광고면
                TimeLineDTO.POST_STATE_ADMOB.toString() ->{
                    binding.itemTimelinePostConstNoticeBar.visibility = View.VISIBLE
                }
                //삭제된 상태면
                TimeLineDTO.POST_STATE_DELETE.toString() ->{

                }
                //페이스북 오디언스면
                TimeLineDTO.POST_STATE_FACEBOOK_AUDIANCE.toString() ->{
                    binding.itemTimelinePostConstNoticeBar.visibility = View.VISIBLE
                }
                //친구 전용이면
                TimeLineDTO.POST_STATE_ONLYFRIENDS.toString() ->{

                }
                //비공개 글이면
                TimeLineDTO.POST_STATE_PRIVATE.toString() ->{

                }
                //전체 공개글이면
                TimeLineDTO.POST_STATE_PUBLIC.toString() ->{

                }
            }

            //친구가 아니면 댓글 창 꺼주기

            //친구면 댓글창 켜주기

            //댓글이 3개 이하면 댓글 미리 보기 리사이클러뷰 켜기

            //댓글이 0개면 댓글 텍스트 끄기
            binding.itemtimelinepost = data
        }
    }

}