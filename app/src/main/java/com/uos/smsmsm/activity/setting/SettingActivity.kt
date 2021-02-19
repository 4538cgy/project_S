package com.uos.smsmsm.activity.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    lateinit var binding : ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting)
        binding.setting = this@SettingActivity

        binding.activitySettingImagebuttonBack.setOnClickListener {
            finish()
        }

    }
}