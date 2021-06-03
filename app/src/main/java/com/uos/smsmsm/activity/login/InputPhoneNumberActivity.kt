package com.uos.smsmsm.activity.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityInputPhoneNumberBinding

class InputPhoneNumberActivity : BaseActivity<ActivityInputPhoneNumberBinding>(R.layout.activity_input_phone_number) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_input_phone_number)
        binding.activityinputphonenumber = this


    }
}