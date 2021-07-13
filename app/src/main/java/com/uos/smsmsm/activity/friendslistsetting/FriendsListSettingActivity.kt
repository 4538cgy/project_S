package com.uos.smsmsm.activity.friendslistsetting

import android.os.Bundle
import android.view.Window
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityFriendsListSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
// Apply ktlint
class FriendsListSettingActivity : BaseActivity<ActivityFriendsListSettingBinding>( R.layout.activity_friends_list_setting) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 타이틀바 없애기
        binding.apply {
            friendslistsetting = this@FriendsListSettingActivity

        }
    }
}
