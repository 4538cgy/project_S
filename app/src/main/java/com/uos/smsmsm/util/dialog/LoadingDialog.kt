package com.uos.smsmsm.util.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.DialogProgressLoadingBinding
import com.uos.smsmsm.databinding.DialogProgressLoadingTextBinding

class LoadingDialog(context: Context) : Dialog(context) {

    lateinit var binding : DialogProgressLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_progress_loading,null,false)
        setContentView(binding.root)
    }
}