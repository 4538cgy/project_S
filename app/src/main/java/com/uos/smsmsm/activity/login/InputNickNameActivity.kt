package com.uos.smsmsm.activity.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityInputNickNameBinding

class InputNickNameActivity : BaseActivity<ActivityInputNickNameBinding>(R.layout.activity_input_nick_name) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityinputnickname = this@InputNickNameActivity
        }
    }
}