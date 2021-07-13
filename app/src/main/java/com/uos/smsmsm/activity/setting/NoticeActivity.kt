package com.uos.smsmsm.activity.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityNoticeBinding

class NoticeActivity : BaseActivity<ActivityNoticeBinding>(R.layout.activity_notice) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.activitynotice = this

        binding.apply {


            initRecyclerData()

            activityNoticeRecycler.adapter
            activityNoticeRecycler.layoutManager = LinearLayoutManager(root.context,LinearLayoutManager.VERTICAL,false)

            activityNoticeImagebuttonClose.setOnClickListener {
                finish()
            }
        }
    }

    private fun initRecyclerData(){

    }
}