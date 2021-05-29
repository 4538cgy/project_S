package com.uos.smsmsm.fragment.tabmenu.userfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.databinding.FragmentUserBinding
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserFragment : BaseFragment<FragmentUserBinding>(R.layout.fragment_user) {

    companion object {
        // var -> const val
        const val PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentuser = this
    }
    // 프로필 변경 이벤트
    fun clickProfilePhoto(view: View) {
    }
}
