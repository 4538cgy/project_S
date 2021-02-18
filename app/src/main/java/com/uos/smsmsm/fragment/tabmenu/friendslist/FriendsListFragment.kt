package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentFriendsListBinding


class FriendsListFragment : Fragment() {

    lateinit var binding : FragmentFriendsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_friends_list,container,false)

        return binding.root
    }


}