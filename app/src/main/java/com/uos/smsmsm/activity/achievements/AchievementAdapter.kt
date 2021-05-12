package com.uos.smsmsm.activity.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ItemAchievementsBinding

/**
 * Created by SungBin on 5/12/21.
 */

class AchievementAdapter(private val achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAchievementsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: Achievement) {
            with(binding) {
                this.achievement = achievement
                ivAchievementIcon.setImageResource(
                    when (achievement.type) {
                        AchievementType.BestMember -> R.drawable.ic_baseline_auto_awesome_24
                        else -> R.drawable.ic_baseline_emoji_people_24
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_achievements, parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount() = achievements.size
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
}
