package com.uos.smsmsm.activity.search

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ActivitySearchFriendBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

// Apply ktlint
@AndroidEntryPoint
class SearchFriendActivity : BaseActivity<ActivitySearchFriendBinding>( R.layout.activity_search_friend) {

    private val viewModel : SNSUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            searchfriend = this@SearchFriendActivity
            viewmodel = viewModel
        }
        //테스트 유저 목록 긁어오기
        viewModel.getAllUserSearchResult()
        initRecyclerView()

    }

    private fun initRecyclerView(){
        val list = MutableLiveData<ArrayList<RecyclerDefaultModel>>()

        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
                = Observer { data ->

            list.value = data
            binding.activitySearchFriendRecycler.apply {
                adapter = MultiViewTypeRecyclerAdapter(rootContext, list)
                layoutManager = LinearLayoutManager(rootContext, LinearLayoutManager.VERTICAL, false)
            }
        }
        viewModel.recyclerData.observe(this,recyclerObserver)
    }

    //뒤로가기
    fun onBack(view: View){
        finish()
    }


}
