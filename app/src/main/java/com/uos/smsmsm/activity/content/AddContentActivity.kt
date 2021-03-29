package com.uos.smsmsm.activity.content

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityAddContentBinding
import com.uos.smsmsm.viewmodel.ContentUtilViewModel

class AddContentActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddContentBinding
    private val viewModel : ContentUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_content)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
    }
}