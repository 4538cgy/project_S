package com.uos.smsmsm.viewmodel

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

//신고하기 등과 같은 간단하면서 자주 쓰이지 않기도하고 메인 기능과 연관이 매우적은 기능이 담기는 viewmodel
class BasicUtilViewModel  @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle, @ApplicationContext private val  appContext : Context) : ViewModel(){

}