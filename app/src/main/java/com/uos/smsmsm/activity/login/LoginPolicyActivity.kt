package com.uos.smsmsm.activity.login

import android.os.Bundle
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityLoginPolicyBinding

class LoginPolicyActivity : BaseActivity<ActivityLoginPolicyBinding>(R.layout.activity_login_policy) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activitypolish = this@LoginPolicyActivity
        }
    }
}