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

    override fun onCreate(svedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_friend)
        binding.searchfriend = this
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel

        //테스트 유저 목록 긁어오기
        viewmodel.getTestUserSearchResult()

        viewmodel.searchUserResult.observe(this, Observer {
            var arrayList = arrayListOf<RecyclerDefaultModel>()

            var data = MutableLiveData<ArrayList<RecyclerDefaultModel>>()


            it.forEachIndexed { index, userDTO ->


                data.value?.add(
                    RecyclerDefaultModel(
                        RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
                        "", null, it[index].userName!!,null
                    )
                )
            }

            startActivity(Intent(binding.root.context,TimeLineFragment::class.java))

            binding.activitySearchFriendRecycler.adapter = MultiViewTypeRecyclerAdapter(binding.root.context,data)
            binding.activitySearchFriendRecycler.layoutManager = RecyclerView.LayoutManager
        })


    }

    //뒤로가기
    fun onBack(view: View){
        finish()
    }


}
