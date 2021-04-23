package com.uos.smsmsm.util.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.uos.smsmsm.R

class LoadingDialog(context: Context) : Dialog(context) {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress_loading)
    }
}