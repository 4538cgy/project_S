package com.uos.smsmsm.activity.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.chat.ChatActivity
import com.uos.smsmsm.activity.chat.LegacyChatActivity
import com.uos.smsmsm.databinding.ActivityProfileBinding
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel

class ProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileBinding
    private val viewModel : UserUtilViewModel by viewModels()

    var destinationUid : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.activityprofile = this

        destinationUid = intent.getStringExtra("uid")


        //친구인지 아닌지 구분
        viewModel.checkFriend(destinationUid.toString())
        viewModel.checkFriends.observe(this, Observer {

            isFriend(it)
        })

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

    fun initDestinationUserData(){
        viewModel.initDestinationUser(destinationUid.toString())
    }

    fun addFriend(view : View){ viewModel.addFriend(destinationUid.toString())}

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