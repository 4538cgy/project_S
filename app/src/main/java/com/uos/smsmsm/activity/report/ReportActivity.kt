package com.uos.smsmsm.activity.report

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.data.ReportDTO
import com.uos.smsmsm.databinding.ActivityReportBinding
import com.uos.smsmsm.viewmodel.BasicUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel

class ReportActivity : AppCompatActivity() {

    lateinit var binding : ActivityReportBinding
    private val basicViewmodel : BasicUtilViewModel by viewModels()
    private val snsViewmodel : SNSUtilViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_report)
        binding.activityreport = this
        binding.viewmodel = basicViewmodel
        binding.snsviewmodel = snsViewmodel
        binding.lifecycleOwner = this
        //액티비티가 시작하고 잘못 클릭해서 허위 제출을 막기위해 제출하기 버튼 비활성화
        buttonSetting()

        basicViewmodel.reportCauseRadioButton.observe(this, Observer {
            basicViewmodel.radioButton(it)
            buttonSetting()
        })

        snsViewmodel.edittextText.observe(this, Observer {
            basicViewmodel.reportEtcCase.postValue(it)
        })

        basicViewmodel.etcEdittextVisibility.observe(this, Observer {
            edittextVisible(it)
        })
    }

    fun buttonSetting(){
        if (basicViewmodel.reportCauseRadioButton.value == null){
            binding.activityReportButtonSend.isEnabled = false
            binding.activityReportButtonSend.text = "사유를 선택해주세요"
            binding.activityReportButtonSend.setBackgroundResource(R.drawable.background_round_dark_gray)
            binding.activityReportButtonSend.setTextColor(Color.WHITE)
        }else{
            binding.activityReportButtonSend.isEnabled = true
            binding.activityReportButtonSend.text = "제출 하기"
            binding.activityReportButtonSend.setBackgroundResource(R.drawable.background_round_white)
            binding.activityReportButtonSend.setTextColor(Color.BLACK)
        }
    }


    fun sendReport(view: View){

        var builder = AlertDialog.Builder(binding.root.context)


        builder.apply {
            setMessage("악의적인 신고 혹은 허위 신고의 경우는 불이익을 얻을 수 있습니다. \n 이에 동의하시나요? \n 동의하신다면 '예'를 눌러 제출해주세요.")
            setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                var data = ReportDTO.USER()

                data.destinationUid = intent.getStringExtra("destinationUid")
                data.reportExplain = basicViewmodel.reportCause.value.toString()
                data.reportUserUid = auth.uid.toString()
                data.timestamp = System.currentTimeMillis()
                if (basicViewmodel.reportEtcCase.value != null){
                    data.etcCause = basicViewmodel.reportEtcCase.value.toString()
                }else{
                    data.etcCause = "not Etc"
                }

                basicViewmodel.saveUserReport(data)

                finish()
            })
            setNegativeButton("아니오" , DialogInterface.OnClickListener { dialog, which ->
                return@OnClickListener

            })
            setTitle("주의사항")
            show()
        }


    }

    fun onBackPressed(view : View){
        finish()
    }

    fun edittextVisible(boolean: Boolean){
        if (boolean) { binding.activityReportEdittextCauseEtc.visibility = View.VISIBLE } else { binding.activityReportEdittextCauseEtc.visibility = View.GONE }
    }
}
