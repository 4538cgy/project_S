package com.uos.smsmsm.viewmodel

import android.content.Context
import android.view.View
import androidx.databinding.Bindable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uos.smsmsm.R
import com.uos.smsmsm.data.ReportDTO
import com.uos.smsmsm.repository.UtilRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//신고하기 등과 같은 간단하면서 자주 쓰이지 않기도하고 메인 기능과 연관이 매우적은 기능이 담기는 viewmodel
class BasicUtilViewModel  @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(){

    var reportCauseRadioButton = MutableLiveData<Int>()
    var reportCause = MutableLiveData<String>()
    var reportEtcCase = MutableLiveData<String>()
    var etcEdittextVisibility = MutableLiveData<Boolean>()

    private val utilRepository = UtilRepository()

    fun radioButton(id : Int ){
        when(id){
            R.id.activity_report_radiobutton_cause_block_message ->{
                reportCause.postValue("유해성 메세지")
                etcEdittextVisibility.postValue(false)
            }
            R.id.activity_report_radiobutton_cause_block_info ->{
                reportCause.postValue("불법 정보")
                etcEdittextVisibility.postValue(false)
            }
            R.id.activity_report_radiobutton_cause_etc ->{
                reportCause.postValue("기타 정보")
                etcEdittextVisibility.postValue(true)
            }
        }
    }

    fun saveUserReport(userReportData : ReportDTO.USER){
        println("세이브 유저 신고 실행!")
        viewModelScope.launch(Dispatchers.IO){
            utilRepository.saveUserReport(userReportData).collect{
                println("세이브 유저 신고! 결과 !$it")
            }
        }
    }



}