package com.uos.smsmsm.activity.friendslistsetting

import android.app.Activity
import android.os.Bundle
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityFriendsListSettingBinding
import com.uos.smsmsm.util.workmanager.BackgroundWorker

// Apply ktlint
class FriendsListSettingActivity : Activity() {

    lateinit var binding: ActivityFriendsListSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_friends_list_setting)
        binding.friendslistsetting = this@FriendsListSettingActivity


    }
}
