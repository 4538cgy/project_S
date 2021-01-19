package com.uos.smsmsm.activity.login

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class LoginViewModel() : ViewModel(){

    val phoneNumber = ObservableField<String>("")

    init {

    }

}