package com.uos.smsmsm.activity.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.chat.LegacyChatActivity
import com.uos.smsmsm.activity.report.ReportActivity
import com.uos.smsmsm.databinding.ActivityProfileBinding
import com.uos.smsmsm.ui.photo.PhotoViewActivity
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel

class ProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileBinding
    private val viewModel : UserUtilViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    var destinationUid : String ? = null
    private var destinationUserProfileUrl : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.activityprofile = this

        destinationUid = intent.getStringExtra("uid")

        //유저 닉네임 가져오기
        viewModel.getUserName(destinationUid.toString())
        viewModel.userName.observe(this, Observer {
            binding.activityProfileTextviewNickname.text = it
        })

        //유저 프로필 사진 가져오기
        viewModel.getUserProfile(destinationUid.toString())
        viewModel.profileImage.observe(this, Observer {
            destinationUserProfileUrl = it.toString()
            Glide.with(binding.root.context)
                .load(it.toString())
                .circleCrop()
                .into(binding.activityProfileImageviewProfile)
        })

        //본인인지 아닌지 구분
        isMe(destinationUid.toString())



        initDestinationUserData()

        viewModel.currentDestinationUser.observe(
            this,
            Observer {
                binding.activityProfileTextviewNickname.text = it.userName

                Glide.with(binding.root.context)
                    .load("https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/userProfileImages%2F3Mxl3osZxGW3eghWwy8FJFVEEPt2?alt=media&token=19080e5e-6cca-4f1e-a056-0e59740c3d43")
                    .apply(
                        RequestOptions().centerCrop().circleCrop()
                    )
                    .into(binding.activityProfileImageviewProfile)
            }
        )

    }

    fun showOptionPopup(view : View){
        PopupMenu(binding.root.context,view).apply {
            setOnMenuItemClickListener { 
                when(it.itemId){
                    R.id.popup_profile_option_report ->{
                        startActivity(Intent(binding.root.context,ReportActivity::class.java))
                    }
                }
                false
            }
            inflate(R.menu.popup_profile_option)
            show()
        }
    }

    fun onClickProfileImage(view:View){
        var intent = Intent(binding.root.context, PhotoViewActivity::class.java)
        intent.apply {
            putExtra("imageUrl",destinationUserProfileUrl)
            startActivity(intent)
        }
    }


    fun initDestinationUserData(){
        viewModel.initDestinationUser(destinationUid.toString())
    }

    //친구 추가 버튼
    fun addFriend(view : View){ viewModel.addFriend(destinationUid.toString())}

    fun isMe(uid : String){
        if (uid == auth.currentUser?.uid){
            binding.activityProfileConstBottomBarIsmeLayout.visibility = View.VISIBLE
            binding.activityProfileConstBottomBarIsfriendLayout.visibility = View.GONE
            binding.activityProfileConstBottomBarIsnotfriendLayout.visibility = View.GONE
        }else{
            binding.activityProfileConstBottomBarIsmeLayout.visibility = View.GONE
            //친구인지 아닌지 구분
            viewModel.checkFriend(destinationUid.toString())
            viewModel.checkFriends.observe(this, Observer {

                isFriend(it)
            })
        }
    }

    fun isFriend(boolean: Boolean){

        if (boolean) {
            binding.activityProfileConstBottomBarIsfriendLayout.visibility = View.VISIBLE
            binding.activityProfileConstBottomBarIsnotfriendLayout.visibility = View.GONE
        }else {
            binding.activityProfileConstBottomBarIsfriendLayout.visibility = View.GONE
            binding.activityProfileConstBottomBarIsnotfriendLayout.visibility = View.VISIBLE
        }
    }

    fun openChat(view: View){

        var intent = Intent(binding.root.context,ChatActivity::class.java)
        intent.apply {
            putExtra("destinationUid",destinationUid)

            startActivity(intent)
        }
    }
}