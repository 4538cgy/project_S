package com.uos.smsmsm.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.EventLog
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.uos.smsmsm.util.EventLogUtil
import com.uos.smsmsm.util.dialog.LoadingDialog
import com.uos.smsmsm.util.dialog.LoadingDialogText
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.manager.ActivityManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

abstract class BaseActivity<B : ViewDataBinding>(val layoutId: Int) : AppCompatActivity() {

    lateinit var binding: B
    lateinit var rootContext : Context
    lateinit var loadingDialog: LoadingDialog
    lateinit var loadingDialogText: LoadingDialogText
    lateinit var progressDialog: ProgressDialogPhoneAuthLoading
    @Inject
    lateinit var activityManager: ActivityManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        activityManager.addActivity(this)
        binding = DataBindingUtil.setContentView(this, layoutId)
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
        //파베에 이벤트 등록
        EventLogUtil().sendScreenName(this.javaClass.simpleName, this)
    }


    override fun onDestroy() {
        super.onDestroy()
//        activityManager.removeActivity(this)
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}