package com.uos.smsmsm.fragment.tabmenu.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentOtherMenuBinding


class OtherMenuFragment : Fragment() {

    lateinit var binding : FragmentOtherMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_other_menu,container,false)

        return binding.root
    }


}