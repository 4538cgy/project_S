package com.uos.smsmsm.fragment.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragmentprofile = this@ProfileFragment
        }
    }

}