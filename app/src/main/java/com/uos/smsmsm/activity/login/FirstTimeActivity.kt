package com.uos.smsmsm.activity.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityFirstTimeBinding

class FirstTimeActivity : BaseActivity<ActivityFirstTimeBinding>(R.layout.activity_first_time) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityfiresttime = this@FirstTimeActivity
        }
    }
}