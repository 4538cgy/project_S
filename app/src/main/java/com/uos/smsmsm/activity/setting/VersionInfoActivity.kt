package com.uos.smsmsm.activity.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityVersionInfoBinding

class VersionInfoActivity : BaseActivity<ActivityVersionInfoBinding>(R.layout.activity_version_info) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.activityversioninfo = this

        binding.apply {

            //뒤로가기
            activityVersionInfoImagebuttonClose.setOnClickListener {
                finish()
            }

            //현재 버전
            activityVersionInfoTextviewCurrentVersion.text = getCurrentVersion()
            //최신 버전
            activityVersionInfoTextviewRecentVersion.text = getRecentVersion()

            //버튼 활성화
            if (getCurrentVersion() != getRecentVersion()){
                activityVersionInfoButtonUpdateVersion.isEnabled = true
            }

            //업데이트 버튼
            activityVersionInfoButtonUpdateVersion.setOnClickListener {
                updateVersion()
            }
        }
    }

    private fun updateVersion(){

    }

    private fun getCurrentVersion() : String {

        return "1.0.0"
    }

    private fun getRecentVersion() : String {

        return "1.0.2"
    }
}