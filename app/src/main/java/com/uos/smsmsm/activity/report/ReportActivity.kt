package com.uos.smsmsm.activity.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityReportBinding
import com.uos.smsmsm.viewmodel.BasicUtilViewModel

class ReportActivity : AppCompatActivity() {

    lateinit var binding : ActivityReportBinding
    private val viewmodel : BasicUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_report)
        binding.activityreport = this
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this


    }

    fun sendReport(view: View){

    }

    fun onBackPressed(view : View){
        finish()
    }
}