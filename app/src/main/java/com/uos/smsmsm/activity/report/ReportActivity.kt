package com.uos.smsmsm.activity.report

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.ReportDTO
import com.uos.smsmsm.databinding.ActivityReportBinding
import com.uos.smsmsm.viewmodel.BasicUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel

class ReportActivity : BaseActivity<ActivityReportBinding>(R.layout.activity_report) {

    private val basicViewModel : BasicUtilViewModel by viewModels()
    private val snsViewModel : SNSUtilViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityreport = this@ReportActivity
            viewmodel = basicViewModel
            snsviewmodel = snsViewModel
            lifecycleOwner = this@ReportActivity
        }

        //액티비티가 시작하고 잘못 클릭해서 허위 제출을 막기위해 제출하기 버튼 비활성화
        buttonSetting()

        basicViewModel.reportCauseRadioButton.observe(this, Observer {
            basicViewModel.radioButton(it)
            buttonSetting()
        })

        snsViewModel.edittextText.observe(this, Observer {
            basicViewModel.reportEtcCase.postValue(it)
        })

        basicViewModel.etcEdittextVisibility.observe(this, Observer {
            edittextVisible(it)
        })
    }

    fun buttonSetting(){
        binding.activityReportButtonSend.apply {
            if (basicViewModel.reportCauseRadioButton.value == null){
                isEnabled = false
                text = "사유를 선택해주세요"
                setBackgroundResource(R.drawable.background_round_dark_gray)
                setTextColor(Color.WHITE)
            }else{
                isEnabled = true
                text = "제출 하기"
                setBackgroundResource(R.drawable.background_round_white)
                setTextColor(Color.BLACK)
            }
        }
    }


    fun sendReport(view: View){

        val builder = AlertDialog.Builder(rootContext)


        builder.apply {
            setMessage("악의적인 신고 혹은 허위 신고의 경우는 불이익을 얻을 수 있습니다. \n 이에 동의하시나요? \n 동의하신다면 '예'를 눌러 제출해주세요.")
            setPositiveButton("예" , DialogInterface.OnClickListener { dialog, which ->
                val data = ReportDTO.USER()

                data.destinationUid = intent.getStringExtra("destinationUid")
                data.reportExplain = basicViewModel.reportCause.value.toString()
                data.reportUserUid = auth.uid.toString()
                data.timestamp = System.currentTimeMillis()
                if (basicViewModel.reportEtcCase.value != null){
                    data.etcCause = basicViewModel.reportEtcCase.value.toString()
                }else{
                    data.etcCause = "not Etc"
                }

                basicViewModel.saveUserReport(data)

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
