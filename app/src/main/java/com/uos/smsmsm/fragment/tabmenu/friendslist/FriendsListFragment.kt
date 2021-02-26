package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.friendslistsetting.FriendsListSettingActivity
import com.uos.smsmsm.activity.search.SearchFriendActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentFriendsListBinding
import com.uos.smsmsm.fragment.tabmenu.friendslist.adapter.FriendsMultiViewTypeRecyclerAdapter


class FriendsListFragment : Fragment() {

    lateinit var binding : FragmentFriendsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends_list,container,false)
        binding.fragmentfriendslist = this

        val list = arrayListOf<RecyclerDefaultModel>(
            RecyclerDefaultModel(RecyclerDefaultModel.TEXT_TYPE_2,"으애앸",R.drawable.btn_signin_google,"내용입니다"),
            RecyclerDefaultModel(RecyclerDefaultModel.TEXT_TYPE,"제목만 있습니다.",0,null),
            RecyclerDefaultModel(RecyclerDefaultModel.IMAGE_TYPE,"사진도있습니다..",R.drawable.btn_signin_google,null),
            RecyclerDefaultModel(RecyclerDefaultModel.IMAGE_TYPE_2,"사진 내용 제목 다 있습니다..",R.drawable.btn_signin_google,"으아아아아")

        )

        binding.fragmentFriendsListRecycler.adapter = FriendsMultiViewTypeRecyclerAdapter(binding.root.context,list)
        binding.fragmentFriendsListRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)

        return binding.root
    }


    fun openSearhActivity(view : View) {startActivity(Intent(binding.root.context, SearchFriendActivity::class.java))}

    fun openFriendListSettingActivity(view : View) {startActivityForResult((Intent(binding.root.context, FriendsListSettingActivity::class.java)),1)}

}