package com.uos.smsmsm.activity.friendslistsetting

import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.setting.SettingActivity
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

            
            //전체 설정 버튼
            activityFriendsListSettingButtonAllSetting.setOnClickListener {
                startActivity(Intent(root.context,SettingActivity::class.java))
            }
            
            //닫기 버튼
            activityFriendsListSettingImagebuttonClose.setOnClickListener { 
                finish()
            }
        }
    }
}
