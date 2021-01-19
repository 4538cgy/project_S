package com.uos.smsmsm.activity.login

import android.app.Application
import android.provider.Settings.Global.getString
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R

class LoginViewModel() : ViewModel(){

    var auth = FirebaseAuth.getInstance()



    val phoneNumber = ObservableField<String>("")

    init {

    }

    fun googlelogin(){

    }

}