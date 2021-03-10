package com.uos.smsmsm.activity.setting

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ActivitySettingBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.AppUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding

    private val viewmodel : AppUtilViewModel by viewModels()
    // ArrayList -> MutableList and make private [list, list2]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.lifecycleOwner = this
        binding.utilviewmodel = viewmodel

        /*
        binding.activitySettingRecycler.adapter =
            MultiViewTypeRecyclerAdapter(binding.root.context, list)
        binding.activitySettingRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

         */
    }

    fun onBack(view: View) {
        finish()
    }
}
