package com.uos.smsmsm.activity.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.ActivitySearchFriendBinding
import com.uos.smsmsm.recycleradapter.friends.FriendListSearchAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

// Apply ktlint
@AndroidEntryPoint
class SearchFriendActivity : BaseActivity<ActivitySearchFriendBinding>( R.layout.activity_search_friend) {

    private val viewModel : SNSUtilViewModel by viewModels()
    private var findList : ArrayList<RecyclerDefaultModel> = ArrayList<RecyclerDefaultModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            searchfriend = this@SearchFriendActivity
            viewmodel = viewModel
            friendlist = SNSUtilViewModel.friendsList
            if(SNSUtilViewModel.friendsList.isEmpty() || SNSUtilViewModel.friendsList.size == 0){
                activitySearchFriendNoFriendText.visibility = View.VISIBLE
                activitySearchFriendRecycler.visibility = View. GONE
            }else{
                activitySearchFriendNoFriendText.visibility = View.GONE
                activitySearchFriendRecycler.visibility = View. VISIBLE
            }
        }
        binding.activitySearchFriendRecycler.apply {
            adapter = FriendListSearchAdapter(rootContext)
        }
        binding.activitySearchFriendSearchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                Log.d("TEST","query: $query")
                //검색 버튼 클릭시
                findList.clear()
                query?.let {
                    for (i in SNSUtilViewModel.friendsList) {
                        if (i.title.contains(query, true)) {
                            findList.add(i)
                        }
                    }
                    if(findList.isEmpty()){
                        binding.run {
                            activitySearchFriendNoFriendText.visibility = View.VISIBLE
                            activitySearchFriendRecycler.visibility = View.GONE
                            friendlist = findList
                        }
                    }else {
                        binding.run {
                            activitySearchFriendNoFriendText.visibility = View.GONE
                            activitySearchFriendRecycler.visibility = View.VISIBLE
                            friendlist = findList
                        }
                    }
                }?:{
                    binding.run {
                        activitySearchFriendNoFriendText.visibility = View.VISIBLE
                        activitySearchFriendRecycler.visibility = View.GONE
                    }
                }()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //검색어 변경 시
                Log.d("TEST","newText: $newText")

                return true
            }

        })
    }

//    private fun initRecyclerView(){
//        val list = MutableLiveData<ArrayList<RecyclerDefaultModel>>()
//
//        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
//                = Observer { data ->
//
//            list.value = data
//
//        }
//        viewModel.recyclerData.observe(this,recyclerObserver)
//    }

    //뒤로가기
    fun onBack(view: View){
        finish()
    }

}
