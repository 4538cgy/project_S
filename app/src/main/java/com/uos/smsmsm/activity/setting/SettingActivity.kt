package com.uos.smsmsm.activity.setting

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivitySettingBinding
import com.uos.smsmsm.util.auth.SignOut
import com.uos.smsmsm.viewmodel.AppUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>(R.layout.activity_setting) {

    private val viewModel: AppUtilViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            utilviewmodel = viewModel
            activitySettingRecycler.adapter = SettingAdapter(
                listOf(
                    SettingItem(SettingItemType.LOGOUT, R.drawable.ic_outline_person_off_24, "로그아웃"),
                    SettingItem(SettingItemType.NOTICE, R.drawable.ic_outline_campaign_24, "공지사항"),
                    SettingItem(SettingItemType.VERSION, R.drawable.ic_outline_info_24, "버전정보", true),
                    SettingItem(SettingItemType.INDICATE),
                    SettingItem(SettingItemType.NOTIFICATION, R.drawable.ic_outline_notifications_24, "알림"),
                    SettingItem(SettingItemType.FRIEND, R.drawable.ic_outline_person_off_24, "친구"),
                    SettingItem(SettingItemType.CHAT, R.drawable.ic_outline_chat_24, "채팅"),
                    SettingItem(SettingItemType.CHATGROUP, R.drawable.ic_outline_dashboard_24, "채팅 모아보기"),
                    SettingItem(SettingItemType.OWNSTORY, R.drawable.ic_outline_space_dashboard_24, "내가 올린 글 모아보기", true),
                    SettingItem(SettingItemType.INDICATE),
                    SettingItem(SettingItemType.THEME, R.drawable.ic_outline_color_lens_24, "테마"),
                    SettingItem(SettingItemType.OTHER, R.drawable.ic_outline_list_24, "기타"),
                    SettingItem(SettingItemType.HELP, R.drawable.ic_baseline_help_outline_24, "고객센터 도움말"),
                )
            ).setOnSettingItemClickListener {
                Log.i("setting clicked", name.toString())

                when (name.toString()) {
                    "로그아웃" -> {
                        SignOut(rootContext,this@SettingActivity)?.signOut()

                    }
                }
            }

            activitySettingImagebuttonBack.setOnClickListener {
                finish()
            }
        }
    }


}
