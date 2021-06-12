package com.uos.smsmsm.activity.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.login.FirstTimeActivity
import com.uos.smsmsm.activity.login.LoginActivity
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityWelcomeMainBinding
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.isPermitted
import com.uos.smsmsm.util.shareddate.PreferenceUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeMainActivity : BaseActivity<ActivityWelcomeMainBinding>(R.layout.activity_welcome_main) {

    private val NUM_PAGES = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activitywelcomemain = this@WelcomeMainActivity
            activityWelcomePageViewpager.adapter = SlidePagerAdapter(this@WelcomeMainActivity)
            activityWelcomeMainIndicator.setViewPager(activityWelcomePageViewpager)
            activityWelcomePageViewpager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 3) {
                        activityWelcomeMainButtonAdultCheck.visibility = View.VISIBLE
                    } else {
                        activityWelcomeMainButtonAdultCheck.visibility = View.GONE
                    }
                    if (position == 2) {
                        activityWelcomeMainButtonPermissionCheck.visibility = View.VISIBLE
                    } else {
                        activityWelcomeMainButtonPermissionCheck.visibility = View.GONE
                    }
                    super.onPageSelected(position)
                }
            })
        }
    }
    private inner class SlidePagerAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa){
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment {
            var fragment = Fragment()
            when(position){
                0 -> {fragment = WelcomeFragment() }
                1 -> {fragment = IntroFragment() }
                2 -> {fragment = CheckPermissionFragment()}
                3 -> {fragment = AdultCheckFragment() }
            }
            return fragment
        }
    }
    private fun moveNext(){
        binding.activityWelcomePageViewpager.setCurrentItem(3,true)
    }
    fun onClickAccept(view : View){
        PreferenceUtil(binding.root.context).setString("adultCheck","true")
        startActivity(Intent(binding.root.context, FirstTimeActivity::class.java))
        finish()
    }
    fun onRequestPermission(v: View){
        if (isPermitted( applicationContext, Config.WELCOME_REQUEST_PERMISSION)) {
            moveNext()
        } else {
            ActivityCompat.requestPermissions(
                this, Config.WELCOME_REQUEST_PERMISSION,
                Config.FLAG_WELCOME_REQUEST_PERMISSION
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Config.FLAG_WELCOME_REQUEST_PERMISSION) {
            if (isPermitted(applicationContext, Config.WELCOME_REQUEST_PERMISSION)) {
               moveNext()
            } else {
                Toast.makeText(applicationContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}