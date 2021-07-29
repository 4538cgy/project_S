package com.uos.smsmsm.recycleradapter.customersupport

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.data.FaqDTO
import com.uos.smsmsm.databinding.ItemFaqBinding
import com.uos.smsmsm.util.Delegate

class FaqAdapter(val context : Context, val callback : Delegate.Action1<FaqDTO>) : RecyclerView.Adapter<FAQHolder>(){
    private var list = ArrayList<FaqDTO>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQHolder {
        val holder =  FAQHolder(ItemFaqBinding.inflate(LayoutInflater.from(context), parent, false))

        return holder
    }

    override fun onBindViewHolder(holder: FAQHolder, position: Int) {
        holder.bind(list[position], callback)
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int  = position
    fun setItem(list : ArrayList<FaqDTO>){
        this.list = list
        notifyDataSetChanged()
    }
}

class FAQHolder(val binding : ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : FaqDTO, callback: Delegate.Action1<FaqDTO>){
        binding.item = item
        binding.root.setOnClickListener {
            callback.run(item)
        }
    }
}