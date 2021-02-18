package com.uos.smsmsm.fragment.tabmenu.userfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentUserBinding


class UserFragment : Fragment() {

    lateinit var binding : FragmentUserBinding

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 101
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user,container,false)

        return binding.root
    }


}