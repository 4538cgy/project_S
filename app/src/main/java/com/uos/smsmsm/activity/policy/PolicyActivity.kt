package com.uos.smsmsm.activity.policy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityPolicyBinding
import com.uos.smsmsm.util.extensions.toast

class PolicyActivity : AppCompatActivity() {

    lateinit var binding: ActivityPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_policy)
        binding.policy = this@PolicyActivity

        // 동의
        binding.activityPolicyButtonAccept.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        // 미동의
        binding.activityPolicyButtonReject.setOnClickListener {
            // Apply extension
            toast("미동의시 회원 가입 및 이용이 불가능합니다.")
            finish()
        }
    }
}
