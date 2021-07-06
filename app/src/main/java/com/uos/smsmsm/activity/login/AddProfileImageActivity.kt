package com.uos.smsmsm.activity.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityAddContentBinding
import com.uos.smsmsm.databinding.ActivityAddProfileImageBinding
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.OneClickListener
import com.uos.smsmsm.util.shareddate.PreferenceUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProfileImageActivity : BaseActivity<ActivityAddProfileImageBinding>(R.layout.activity_add_profile_image) {

    private var photoUri : Uri ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityaddprofileimage = this@AddProfileImageActivity
            activityAddProfileImageButtonComplete.isEnabled = false
        }
        binding.activityAddProfileImageImagebuttonProfile.setOnClickListener(object: OneClickListener(){
            override fun onOneClick(v: View?) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                startActivityForResult(intent, Config.FLAG_REQ_GALLERY)
            }

        })
        binding.activityAddProfileImageButtonComplete.setOnClickListener(object:OneClickListener(){
            override fun onOneClick(v: View?) {
                val intent = Intent(binding.root.context,InputPhoneNumberActivity::class.java)
                intent.apply {
                    putExtra("photoUri",photoUri.toString())
                    // 이 부분에서 intent가 방금 생성된 위에 생성된 InputPhoneNumberActivity를 포함하는 intent라 항상 Null 입니당. 어떻게 수정해야할지가 필요해 보입니다.
                    PreferenceUtil(binding.root.context).getString("signUpType","null")
                    startActivity(intent)
                    finish()
                }
            }

        })
    }

    fun interactiveUI(){
        binding.activityAddProfileImageButtonComplete.isEnabled = true
        Glide.with(binding.root.context)
            .load(photoUri)
            .thumbnail(0.01f)
            .circleCrop()
            .into(binding.activityAddProfileImageImagebuttonProfile)
        binding.activityAddProfileImageButtonComplete.setBackgroundResource(R.drawable.background_edittext_round_black_4dp)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Config.FLAG_REQ_GALLERY){
                if (data != null){
                    photoUri = data.data
                    interactiveUI()
                }
            }
        }
    }
}