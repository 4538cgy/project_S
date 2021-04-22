package com.uos.smsmsm.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogAddFriends : BottomSheetDialogFragment() {
    lateinit var bottomSheetButtonClickListener : BottomSheetButtonClickListener
    private lateinit var binding

    interface BottomSheetButtonClickListener { fun onBottomSheetButtonClick(text : String) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}