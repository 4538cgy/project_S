package com.uos.smsmsm.util.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.DialogProgressLoadingTextBinding
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel

class LoadingDialogText(context: Context) : Dialog(context) {


    lateinit var binding : DialogProgressLoadingTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_progress_loading_text,null,false)
        setContentView(binding.root)

    }
}