package com.uos.smsmsm.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.uos.smsmsm.util.EventLogUtil
import com.uos.smsmsm.util.dialog.LoadingDialog
import com.uos.smsmsm.util.dialog.LoadingDialogText
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading

abstract class BaseFragment<B : ViewDataBinding>(val layoutId : Int) : Fragment() {

    lateinit var binding: B
    lateinit var rootContext : Context
    lateinit var loadingDialog: LoadingDialog
    lateinit var loadingDialogText: LoadingDialogText
    lateinit var progressDialog: ProgressDialogPhoneAuthLoading
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this

        rootContext = binding.root.context
        //프로그레스 초기화
        loadingDialog = LoadingDialog(rootContext).apply {
            //프로그레스 투명하게
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //프로그레스 꺼짐 방지
            setCancelable(false)
        }
        //프로그레스 초기화
        loadingDialogText = LoadingDialogText(rootContext).apply {
            //프로그레스 투명하게
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //프로그레스 꺼짐 방지
            setCancelable(false)
        }
        // 로딩 초기화
        progressDialog = ProgressDialogPhoneAuthLoading(rootContext).apply {
            // 프로그레스 투명하게
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 프로그레스 꺼짐 방지
            setCancelable(false)
        }
        // 파베에 이벤트 등록 화면 호출 시 호출됨.
        EventLogUtil().sendScreenName(this.javaClass.simpleName,requireContext())
        init()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if(loadingDialog.isShowing){
            loadingDialog.dismiss()
        }
    }

    protected open fun init(){

    }
}