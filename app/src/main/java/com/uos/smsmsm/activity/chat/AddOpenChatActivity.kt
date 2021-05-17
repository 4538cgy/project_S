package com.uos.smsmsm.activity.chat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.android.material.internal.TextWatcherAdapter
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityAddOpenChatBinding

class AddOpenChatActivity  : BaseActivity<ActivityAddOpenChatBinding>(R.layout.activity_add_open_chat) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.openChat = this
        binding.openChatTitle.addTextChangedListener(TitleTextChangeAdapter);
    }


    //제목 변경 감지
    var TitleTextChangeAdapter = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            var count = binding.openChatTitle.text.count();
            if(count == 0){
                binding.makeOpenChat.isEnabled = false;
            }else{
                binding.makeOpenChat.isEnabled = true;
            }
            binding.titleTextCount.text = count.toString() + "/30" ;
        }
    }

    fun finish(view : View) {
        finish()
    }

    fun ReturnResult(view : View) {
        var intent : Intent = Intent();
        intent.putExtra("OpenChatTitle",binding.openChatTitle.text.toString());
        setResult(RESULT_OK,intent);
        finish()
    }
}