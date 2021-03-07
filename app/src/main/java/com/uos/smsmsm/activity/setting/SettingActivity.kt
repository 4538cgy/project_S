package com.uos.smsmsm.activity.setting

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ActivitySettingBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding

    // ArrayList -> MutableList and make private [list, list2]
    private var list = mutableListOf<RecyclerDefaultModel>()
    private var list2 = mutableListOf<RecyclerDefaultModel.FirendsRecyclerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.setting = this@SettingActivity

        list.add(RecyclerDefaultModel(RecyclerDefaultModel.TEXT_TYPE, "null", null, "로그아웃", "nope"))

        binding.activitySettingRecycler.adapter =
            MultiViewTypeRecyclerAdapter(binding.root.context, list)
        binding.activitySettingRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
    }

    fun onBack(view: View) {
        finish()
    }
}
