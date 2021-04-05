package com.uos.smsmsm.activity.search

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.R
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ActivitySearchFriendBinding
import com.uos.smsmsm.fragment.tabmenu.timeline.TimeLineFragment
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

// Apply ktlint
@AndroidEntryPoint
class SearchFriendActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchFriendBinding
    private val viewmodel : SNSUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_friend)
        binding.searchfriend = this
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel

        //테스트 유저 목록 긁어오기
        viewmodel.getAllUserSearchResult()
        initRecyclerView()

    }

    fun initRecyclerView(){
        var list = MutableLiveData<ArrayList<RecyclerDefaultModel>>()

        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
                = Observer { data ->

            list.value = data
            binding.activitySearchFriendRecycler.adapter = MultiViewTypeRecyclerAdapter(binding.root.context,list)
            binding.activitySearchFriendRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        }
        viewmodel.recyclerData.observe(this,recyclerObserver)
    }

    //뒤로가기
    fun onBack(view: View){
        finish()
    }


}
