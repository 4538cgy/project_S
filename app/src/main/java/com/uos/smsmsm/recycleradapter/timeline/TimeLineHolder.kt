package com.uos.smsmsm.recycleradapter.timeline

import com.uos.smsmsm.base.BaseHolder
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ItemTimelinePostBinding

class TimeLineHolder(binding: ItemTimelinePostBinding) : BaseHolder<ItemTimelinePostBinding, TimeLineDTO>(binding) {
    override fun bind(element: TimeLineDTO) {
        super.bind(element)
        binding.itemtimelinepost = element
    }
}