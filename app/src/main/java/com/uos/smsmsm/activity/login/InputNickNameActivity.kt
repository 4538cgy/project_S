package com.uos.smsmsm.activity.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityInputNickNameBinding

class InputNickNameActivity : BaseActivity<ActivityInputNickNameBinding>(R.layout.activity_input_nick_name) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityinputnickname = this@InputNickNameActivity

            activityInputNickNameEdittext.addTextChangedListener {
                textLenghtInteractive(it!!.length)
            }
        }
    }

    fun textLenghtInteractive(lenght : Int){
        binding.activityInputNickNameTextviewNicknameSize.text = "$lenght/12"

        binding.activityInputNickNameButtonComplete.isEnabled = lenght > 2
    }
    
    fun onComplete(view : View)
    {
        //데이터 업로드하고 로비로 이동
    }
}