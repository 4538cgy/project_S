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
import com.uos.smsmsm.databinding.BottomSheetDialogWriteContentBinding
import java.lang.ClassCastException

class BottomSheetDialogWriteContent  : BottomSheetDialogFragment() {
    lateinit var bottomSheetButtonClickListener : BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetDialogWriteContentBinding

    interface BottomSheetButtonClickListener { fun onBottomSheetButtonClick(text : String) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_write_content,container,false)
        binding.bottomsheetdialogwritecontent = this@BottomSheetDialogWriteContent
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
            binding.bottomSheetDialogWriteContentButtonClose.id ->{ }
            binding.bottomSheetDialogWriteContentConstOnlyMe.id ->{
                println("only me")
            }
            binding.bottomSheetDialogWriteContentConstOnlyFriends.id ->{
                println("only friends")
            }
            binding.bottomSheetDialogWriteContentConstPublic.id ->{
                println("public")
            }
        }
        dismiss()
    }
}