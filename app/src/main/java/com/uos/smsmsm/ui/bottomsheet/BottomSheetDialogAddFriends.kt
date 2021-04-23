package com.uos.smsmsm.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.BottomSheetDialogAddFriendsLayoutBinding
import java.lang.ClassCastException

class BottomSheetDialogAddFriends : BottomSheetDialogFragment() {
    lateinit var bottomSheetButtonClickListener : BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetDialogAddFriendsLayoutBinding

    interface BottomSheetButtonClickListener { fun onBottomSheetButtonClick(text : String) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_add_friends_layout,container,false)
        binding.bottomsheetdialogaddfriendslayout = this@BottomSheetDialogAddFriends
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            bottomSheetButtonClickListener = context as BottomSheetButtonClickListener
        } catch (e: ClassCastException) {
            Log.e("bottomSheet", "Click Listener onAttach Error")
        }
    }

    fun groupClick(view : View){
        when(view.id){
            //qr 코드로 추가
            binding.bottomSheetDialogAddFriendsLayoutButtonCode.id ->{
                println("click code")
            }
            //id 로 추가
            binding.bottomSheetDialogAddFriendsLayoutButtonId.id ->{
                println("click id")
            }
            //연락처로 추가
            binding.bottomSheetDialogAddFriendsLayoutButtonPhoneNumber.id ->{
                println("click number")
            }
            //추천 친구
            binding.bottomSheetDialogAddFriendsLayoutButtonRecommand.id ->{
                println("click recommand")
            }
        }
    }
}