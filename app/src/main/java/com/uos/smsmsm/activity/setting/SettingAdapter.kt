package com.uos.smsmsm.activity.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.LayoutSettingItemBinding

/**
 * Created by SungBin on 3/14/21.
 */

class SettingAdapter(private val items: List<SettingItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnSettingItemClickListener? = null
    private val itemIsIndicate = 0
    private val itemIsSetting = 1

    interface OnSettingItemClickListener {
        fun onItemClick(item: SettingItem)
    }

    fun setOnSettingItemClickListener(action: SettingItem.() -> Unit): SettingAdapter {
        listener = object : OnSettingItemClickListener {
            override fun onItemClick(item: SettingItem) {
                action(item)
            }
        }
        return this
    }

    inner class ItemViewHolder(
        private val binding: LayoutSettingItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingItem) {
            with(binding) {
                this.item = item
                root.setOnClickListener {
                    listener?.onItemClick(item)
                }
            }
        }
    }

    inner class IndicateViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind() {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            itemIsIndicate -> IndicateViewHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.layout_setting_indicate_line, parent, false)
            )
            else -> ItemViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.layout_setting_item,
                    parent,
                    false
                )
            )
        }

    private fun getItem(position: Int) = items[position]

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item.type == SettingItemType.INDICATE) {
            (holder as IndicateViewHolder).bind()
        } else {
            (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) =
        if (getItem(position).type == SettingItemType.INDICATE) itemIsIndicate else itemIsSetting
}
