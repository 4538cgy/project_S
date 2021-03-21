package com.uos.smsmsm.activity.alarm

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityAlarmBinding
import com.uos.smsmsm.viewmodel.AppUtilViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 *  Alaram 화면
 */

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    lateinit var bindng: ActivityAlarmBinding
    private val viewModel: AppUtilViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        bindng = DataBindingUtil.setContentView(this, R.layout.activity_alarm)
        bindng.alarmain = this

        setSupportActionBar(bindng.toolbarAlarmActivity)

        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        bindng.activityAlarmRecycler.adapter = AlarmAdapter(listOf(
            AlarmItem(AlarmType.DATE,null, AlarmItem.HeaderItem(Calendar.getInstance().time)),
            AlarmItem(AlarmType.LIKE_ALARM, AlarmItem.TextAlarmItem("xx님이 xx 글에 좋아요를 누르셨습니다.",""), null),
            AlarmItem(AlarmType.COMMENT_ALARM, AlarmItem.TextAlarmItem("xx님이 내 게시글 xx에 댓글을 남기셨습니다","아 참 이쁘네오"), null),
            AlarmItem(AlarmType.NESTED_COMMENT_ALARM, AlarmItem.TextAlarmItem("xx님이 내 댓글 xx에 대댓글을 남기셨습니다.","그게 뭔 개소리에요 옥상으로 따라와 이새끼야"), null),
            AlarmItem(AlarmType.DM_ALARM, AlarmItem.TextAlarmItem("xx님이 DM을 요청하셨습니다","xx님이 DM을 보내셨습니다\n안녕하세요"), null),
        ),applicationContext)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //toolbar의 back키 눌렀을 때 동작
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}