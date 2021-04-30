package com.uos.smsmsm.recycleradapter.timeline

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemTimelinePostBinding

class TimeLineRecyclerAdapter(private val context : Context, private val list : LiveData<ArrayList<TimeLineDTO>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemTimelinePostBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return TimeLinePostViewHolder(binding = binding)
    }


    override fun getItemCount(): Int = list.value!!.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TimeLinePostViewHolder).onBind(list.value!![position])


    }

    inner class TimeLinePostViewHolder(val binding : ItemTimelinePostBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data : TimeLineDTO){
            binding.itemtimelinepost = data
        }
    }

}