package com.uos.smsmsm.util.manager

import android.content.Intent
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class ActivityModule {

    @Singleton
    @Binds
    abstract fun bindActivityManageService(
        activityManagerImpl: ActivityManagerImpl
    ): ActivityManager
}

class ActivityManagerImpl @Inject constructor() : ActivityManager {

    var activityList = ArrayList<AppCompatActivity>()
    override fun addActivity(activity: AppCompatActivity) {
        activityList.add(activity)
    }

    override fun removeActivity(activity: AppCompatActivity) {
        activityList.remove(activity)
    }

    override fun getActivitySize(): Int = activityList.size

    override fun removeAllBehindActivity(intent: Intent) {
        if (activityList.size <= 0) return

        activityList[activityList.size - 1].startActivity(intent)

        Handler().postDelayed({
            while (activityList.size > 1) {
                activityList[0].finish()
            }
        }, 500)
    }

    override fun removeAllBehindActivity(activity: AppCompatActivity) {
        if (activityList.size <= 0) return

        val intent = Intent(activityList[activityList.size - 1], activity.javaClass)
        activityList[activityList.size - 1].startActivity(intent)

        Handler().postDelayed({
            while (activityList.size > 1) {
                activityList[0].finish()
            }
        }, 500)
    }

    override fun removeAllBehindActivity() {
        Handler().postDelayed({
            while (activityList.size > 1) {
                activityList[0].finish()
                activityList.remove(activityList[0])
            }
        }, 500)
    }

}