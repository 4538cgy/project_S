package com.uos.smsmsm.activity.achievements

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityAchievementsBinding
import com.uos.smsmsm.viewmodel.AppUtilViewModel
import kotlin.random.Random

class AchievementsActivity :
    BaseActivity<ActivityAchievementsBinding>(R.layout.activity_achievements) {

    private val viewModel: AppUtilViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.activityAchievementsRecycler.adapter = AchievementAdapter(
            listOf(
                Achievement(AchievementType.Meeter, Random.nextBoolean()),
                Achievement(AchievementType.BestMember, Random.nextBoolean()),
                Achievement(AchievementType.Meeter, Random.nextBoolean()),
                Achievement(AchievementType.Meeter, Random.nextBoolean()),
                Achievement(AchievementType.Meeter, Random.nextBoolean()),
                Achievement(AchievementType.Meeter, Random.nextBoolean())
            )
        )
    }

    fun onBack(view: View) {
        finish()
    }
}
