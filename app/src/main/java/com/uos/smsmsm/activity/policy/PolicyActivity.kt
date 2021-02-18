package com.uos.smsmsm.activity.policy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityPolicyBinding

class PolicyActivity : AppCompatActivity() {

    lateinit var binding : ActivityPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_policy)
        binding.policy = this@PolicyActivity


        //동의
        binding.activityPolicyButtonAccept.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        //미동의
        binding.activityPolicyButtonReject.setOnClickListener {
            Toast.makeText(binding.root.context , "미동의시 회원 가입 및 이용이 불가능합니다." , Toast.LENGTH_LONG).show()
            finish()
        }


    }
}