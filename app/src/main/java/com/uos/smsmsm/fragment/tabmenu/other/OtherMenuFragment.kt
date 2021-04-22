package com.uos.smsmsm.fragment.tabmenu.other

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.setting.SettingActivity
import com.uos.smsmsm.databinding.FragmentOtherMenuBinding
import com.uos.smsmsm.testactivity.AddTestUser
import com.uos.smsmsm.util.dialog.LoadingDialog
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel

class OtherMenuFragment : Fragment() {

    lateinit var binding: FragmentOtherMenuBinding

    private val viewmodel: UserUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // View? -> View (NonNull)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_other_menu, container, false)
        binding.fragmentothermenu = this

        //프로그레스 초기화
        loadingDialog = LoadingDialog(binding.root.context)
        //프로그레스 투명하게
        loadingDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        loadingDialog!!.setCancelable(false)

        // 설정
        binding.fragmentOtherMenuSettingButton.setOnClickListener {
            startActivity(Intent(binding.root.context, SettingActivity::class.java))
        }

        //유저 프로필 사진 가져오기
        loadingDialog.show()
        viewmodel.getUserProfile(auth.currentUser!!.uid.toString())
        viewmodel.profileImage.observe(viewLifecycleOwner, Observer { Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(binding.fragmentOtherMenuCircle) })

        //유저 닉네임 가져오기
        viewmodel.getUserName(auth.currentUser!!.uid.toString())
        viewmodel.userName.observe(viewLifecycleOwner, Observer { binding.fragmentOtherMenuTextviewProfileNickname.text = it.toString()
        loadingDialog.dismiss()})


        return binding.root
    }

    fun openManagePage(view: View){
        startActivity(Intent(binding.root.context,AddTestUser::class.java))
    }

    fun onClickProfilePhoto(view: View) {
    }

    fun onClickSettingButton(view: View) {
        startActivity(Intent(binding.root.context, SettingActivity::class.java))
    }
}
