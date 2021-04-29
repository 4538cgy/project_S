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
import com.uos.smsmsm.activity.login.LoginActivity
import com.uos.smsmsm.databinding.ActivityWelcomeMainBinding
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.isPermitted
import com.uos.smsmsm.util.shareddate.PreferenceUtil

class WelcomeMainActivity : FragmentActivity() {

    private val NUM_PAGES = 4

    lateinit var binding : ActivityWelcomeMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_main)
        binding.activitywelcomemain = this
        binding.activityWelcomePageViewpager.adapter = SlidePagerAdapter(this)
        binding.activityWelcomeMainIndicator.setViewPager(binding.activityWelcomePageViewpager)
        binding.activityWelcomePageViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if (position == 3){binding.activityWelcomeMainButtonAdultCheck.visibility = View.VISIBLE} else{ binding.activityWelcomeMainButtonAdultCheck.visibility = View.GONE}
                if(position == 2){ binding.activityWelcomeMainButtonPermissionCheck.visibility = View.VISIBLE} else{ binding.activityWelcomeMainButtonPermissionCheck.visibility = View.GONE}
                super.onPageSelected(position)
            }
        })
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
        startActivity(Intent(binding.root.context, LoginActivity::class.java))
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