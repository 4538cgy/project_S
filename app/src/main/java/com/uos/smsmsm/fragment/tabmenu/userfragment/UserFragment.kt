package com.uos.smsmsm.fragment.tabmenu.userfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    lateinit var binding: FragmentUserBinding

    companion object {
        // var -> const val
        const val PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // View? -> View (NonNull)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        binding.fragmentuser = this

        return binding.root
    }

    // 프로필 변경 이벤트
    fun clickProfilePhoto(view: View) {
    }
}
