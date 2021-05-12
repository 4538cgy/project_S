package com.uos.smsmsm.base

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.datatransport.runtime.EncodedPayload

abstract class BaseHolder <VB: ViewDataBinding, E: Any> (protected  val binding: VB): RecyclerView.ViewHolder(binding.root){
    val context: Context
        get() { return itemView.context }

    lateinit var element: E

    open fun bind(element: E) {
        this.element = element

    }

    open fun bind(element: E, payload: MutableList<Any>){
        this.element = element
    }

}