package com.uos.smsmsm.activity.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityAddContentBinding
import com.uos.smsmsm.databinding.ActivityAddProfileImageBinding

class AddProfileImageActivity : BaseActivity<ActivityAddProfileImageBinding>(R.layout.activity_add_profile_image) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityaddprofileimage = this@AddProfileImageActivity
        }
    }
}