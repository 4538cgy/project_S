package com.uos.smsmsm.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.BottomSheetDialogPostMoreOptionBinding

class BottomSheetDialogPostMoreOption : BottomSheetDialogFragment(){
    lateinit var bottomSheetButtonClickListener : BottomSheetDialogAddFriends.BottomSheetButtonClickListener
    private lateinit var binding : BottomSheetDialogPostMoreOptionBinding
    interface BottomSheetButtonClickListener { fun onBottomSheetButtonClick(text : String) }
    private var destinationUid : String ? = null
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog_post_more_option,container,false)
        binding.bottomsheetdialogpostmoreoption = this@BottomSheetDialogPostMoreOption

        var bundle = arguments
        println("으아아아아 ${bundle!!.getString("postId")}")
        println("으에에에에 ${bundle.getString("destinationUid")}")
        destinationUid = bundle.getString("destinationUid")
        println("내 uid ${auth.currentUser!!.uid}")
        if(auth.currentUser!!.uid == destinationUid){
            binding.bottomSheetDialogPostMoreOptionLinearlayoutIsme.visibility = View.VISIBLE
            binding.bottomSheetDialogPostMoreOptionLinearlayoutIsnotme.visibility = View.GONE
        }else{
            binding.bottomSheetDialogPostMoreOptionLinearlayoutIsme.visibility = View.GONE
            binding.bottomSheetDialogPostMoreOptionLinearlayoutIsnotme.visibility = View.VISIBLE
        }

        return binding.root
    }

    fun groupClick(view : View){
        when(view.id){

        }
    }
}