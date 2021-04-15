package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.friendslistsetting.FriendsListSettingActivity
import com.uos.smsmsm.activity.search.SearchFriendActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentFriendsListBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsListFragment : Fragment() {

    lateinit var binding: FragmentFriendsListBinding
    private val viewmodel: SNSUtilViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends_list, container, false)
        binding.fragmentfriendslist = this

        viewmodel.initFriendsList()
        initRecyclerViewAdapter()

        return binding.root
    }
    fun initRecyclerViewAdapter(){
        var data = MutableLiveData<ArrayList<RecyclerDefaultModel>>()

        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
                = Observer { livedata ->
            data.value = livedata
            binding.fragmentFriendsListRecycler.adapter = MultiViewTypeRecyclerAdapter(binding.root.context,data)
            binding.fragmentFriendsListRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        }

        viewmodel.recyclerData.observe(viewLifecycleOwner, recyclerObserver)

    }
    fun openSearhActivity(view: View) {
        startActivity(Intent(binding.root.context, SearchFriendActivity::class.java))
    }

    fun openFriendListSettingActivity(view: View) {
        startActivityForResult(
            (
                    Intent(
                        binding.root.context,
                        FriendsListSettingActivity::class.java
                    )
                    ),
            1
        )
    }
}
