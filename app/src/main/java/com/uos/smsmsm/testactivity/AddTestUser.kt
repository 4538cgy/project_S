package com.uos.smsmsm.testactivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityAddTestUserBinding
import com.uos.smsmsm.viewmodel.manage.ManagementViewModel

// Apply ktlint
class AddTestUser : AppCompatActivity() {

    private val viewModel : ManagementViewModel by viewModels()
    lateinit var binding : ActivityAddTestUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_test_user)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


    }

    fun createButtonClick(){
        viewModel.createUser()
    }
}
