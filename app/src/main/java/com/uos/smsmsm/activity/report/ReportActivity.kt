package com.uos.smsmsm.activity.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityReportBinding
import com.uos.smsmsm.viewmodel.BasicUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel

class ReportActivity : AppCompatActivity() {

    lateinit var binding : ActivityReportBinding
    private val basicViewmodel : BasicUtilViewModel by viewModels()
    private val snsViewmodel : SNSUtilViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_report)
        binding.activityreport = this
        binding.viewmodel = basicViewmodel
        binding.snsviewmodel = snsViewmodel
        binding.lifecycleOwner = this

        basicViewmodel.reportCauseRadioButton.observe(this, Observer {
            basicViewmodel.radioButton(it)
        })

        snsViewmodel.edittextText.observe(this, Observer {
            println("아아아아아앜 $it")
        })

        basicViewmodel.etcEdittextVisibility.observe(this, Observer {
            edittextVisible(it)
        })
    }

    fun sendReport(view: View){
        finish()
    }

    fun onBackPressed(view : View){
        finish()
    }

    fun edittextVisible(boolean: Boolean){
        if (boolean) { binding.activityReportEdittextCauseEtc.visibility = View.VISIBLE } else { binding.activityReportEdittextCauseEtc.visibility = View.GONE }

    }
}
