package com.uos.smsmsm.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.uos.smsmsm.data.FaqDTO
import com.uos.smsmsm.util.SingleLiveEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject

// 앱 기능 관련 ViewModel
class AppUtilViewModel @ViewModelInject constructor() : ViewModel(){

    private val TAG : String = "AppUtilViewModel"
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    val faqListLiveData : SingleLiveEvent<ArrayList<FaqDTO>> by lazy { SingleLiveEvent<ArrayList<FaqDTO>>() }
    val propensityAnalysisScore : SingleLiveEvent<Int> by lazy { SingleLiveEvent<Int>() }

    private lateinit var analysisScoreList: Array<Int>
    fun requestGetFaq(type : String?){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()

            setConfigSettingsAsync(configSettings)
        }
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    type?.let {
                        getTopFaqList(type)
                    } ?: run {
                        getTopFaqList(null)
                    }
                }
            }
    }
    private fun getTopFaqList(uk: String?) {
        val topObject: String = if (uk == null) {
            mFirebaseRemoteConfig.getString("faq_top_object")
        } else {
            mFirebaseRemoteConfig.getString(uk)
        }
        if (topObject.isEmpty()) {
            // FAQ가 준비중입니다. 노출 필요
        } else {
            val topJsonObj = JSONObject(topObject)
            val list: JSONArray = topJsonObj["list"] as JSONArray
            Log.d(TAG, "${list}")
            val gson = Gson()
            val faqlist = ArrayList<FaqDTO>()
            for (i in 0 until list.length()) {
                faqlist.add(gson.fromJson(list[i].toString(), FaqDTO::class.java))
            }
            Log.d(TAG, "${faqlist.size}")
            faqListLiveData.postValue(faqlist)
        }
    }
    fun setScoreList(size : Int){
        analysisScoreList = Array(size) { -1 }
    }
    fun applyNumber(position : Int , score : Int){
        analysisScoreList[position] = score
        checkScoreList()
    }
    fun checkScoreList(){
        var isAllClear = true
        analysisScoreList.forEach {
            if(it < 0){
                isAllClear = false
                return@forEach
            }
        }
        if(isAllClear){
            var total : Int= 0
            for(i in analysisScoreList){
                total += i
            }
            propensityAnalysisScore.postValue(total)
        }
    }

}