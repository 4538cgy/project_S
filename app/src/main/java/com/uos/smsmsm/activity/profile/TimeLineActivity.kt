package com.uos.smsmsm.activity.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityTimeLineBinding
import com.uos.smsmsm.viewmodel.ContentUtilViewModel

class TimeLineActivity : AppCompatActivity() {

    lateinit var binding : ActivityTimeLineBinding
    private val viewModel : ContentUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_line)
        binding.activitytimeline = this@TimeLineActivity
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel


    }

    fun onBack(view: View){ finish() }

    fun selectSubject(view : View){
        when(view.id){
            R.id.activity_time_line_textview_contents ->{
                println("타임라인 게시글로 보여주기")
            }
            R.id.activity_time_line_textview_media ->{
                println("타임라인 미디어로만 보여주기")
            }
        }
    }
}