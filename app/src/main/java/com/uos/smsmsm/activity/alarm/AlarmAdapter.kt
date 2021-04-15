package com.uos.smsmsm.activity.alarm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ItemAlarmViewBinding
import com.uos.smsmsm.databinding.ItemHeaderViewBinding

class AlarmAdapter(var alarmList: List<AlarmItem>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> HeaderHolder(
                ItemHeaderViewBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
            else -> AlarmHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_alarm_view,
                    parent,
                    false
                )
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(alarmList[position].type == AlarmType.DATE){
            (holder as HeaderHolder).onBind(alarmList[position].headerItem!!)
        }else{
            (holder as AlarmHolder).onBind(alarmList[position].textAlarmItem!!)
        }
    }

    override fun getItemCount(): Int = alarmList.size
    override fun getItemViewType(position: Int): Int {
        return when (alarmList[position].type) {
            AlarmType.DATE -> 0
            AlarmType.LIKE_ALARM -> 1
            AlarmType.COMMENT_ALARM -> 2
            AlarmType.NESTED_COMMENT_ALARM -> 3
            AlarmType.DM_ALARM -> 4
            AlarmType.MULTI_LIKE_ALARM -> 5
        }
    }
    fun refresh(alarmList: List<AlarmItem>) {
        this.alarmList = alarmList
        notifyDataSetChanged()
    }

    class HeaderHolder(val binding: ItemHeaderViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item : AlarmItem.HeaderItem){
            binding.headerItem = item
        }
    }

    class AlarmHolder(val binding: ItemAlarmViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item : AlarmItem.TextAlarmItem){
            if(item.secondLineString.isEmpty()){
                binding.isEmpty = true
            }
            binding.alarmitem = item
        }
    }
}

sealed class AlarmType {
    object DATE : AlarmType()
    object LIKE_ALARM : AlarmType()
    object COMMENT_ALARM : AlarmType()
    object NESTED_COMMENT_ALARM : AlarmType()
    object DM_ALARM : AlarmType()
    object MULTI_LIKE_ALARM : AlarmType()
}