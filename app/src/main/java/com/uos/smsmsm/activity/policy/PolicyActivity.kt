package com.uos.smsmsm.activity.policy

import android.os.Bundle
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityPolicyBinding
import com.uos.smsmsm.util.extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyActivity : BaseActivity<ActivityPolicyBinding>(R.layout.activity_policy) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            policy = this@PolicyActivity
            // 동의
            activityPolicyButtonAccept.setOnClickListener {
                setResult(RESULT_OK)
                finish()
            }
            // 미동의
            activityPolicyButtonReject.setOnClickListener {
                // Apply extension
                toast("미동의시 회원 가입 및 이용이 불가능합니다.")
                finish()
            }
        }
    }
}
