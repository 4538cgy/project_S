package com.uos.smsmsm.activity.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.login.FirstTimeActivity
import com.uos.smsmsm.activity.login.LoginActivity
import com.uos.smsmsm.activity.welcome.WelcomeMainActivity
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivitySplashBinding
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.isPermitted
import com.uos.smsmsm.util.shareddate.PreferenceUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.splash = this@SplashActivity


        if (isPermitted( applicationContext, Config.WELCOME_REQUEST_PERMISSION)) {
            firebaseRemoteFetch()
        }else{
            ActivityCompat.requestPermissions(
                    this, Config.WELCOME_REQUEST_PERMISSION,
                    Config.FLAG_WELCOME_REQUEST_PERMISSION
            )
        }
//        activityManager.removeAllBehindActivity()
    }

    // 앱버전 체크
    // Make private and apply Camelcase
    private fun appVersionCheckWithRemoteConfig() = this.packageManager.getPackageInfo(
        this.packageName,
        PackageManager.GET_ACTIVITIES
    ).versionName
    private fun firebaseRemoteFetch(){
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.fetch(0).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseRemoteConfig.fetchAndActivate()
                dialogDisplay(firebaseRemoteConfig)
            } else {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("앱 실행에 오류가 발생하였습니다. 다시 실행해주시기 바랍니다.")
                        .setCancelable(false)
                        .setPositiveButton(
                                "종료"
                        ) { _, _ -> // Apply SAM
                            this.finishAffinity()
                        }.show()
            }
        }.addOnFailureListener {
            Log.e("TEST","${it.toString()}")
        }
    }

    // Make private and apply Camelcase
    private fun dialogDisplay(firebaseRemoteConfig: FirebaseRemoteConfig) {
        val strVersionName = appVersionCheckWithRemoteConfig()
        // version from Firebase
        val strLatestVersion = firebaseRemoteConfig.getString("message_version")
        val strMaintenanceCheck = firebaseRemoteConfig.getBoolean("check_maintenance")

        if (strMaintenanceCheck) {
            dialogDisplayDownServer()
        } else {
            if (strVersionName != strLatestVersion) {
                AlertDialog.Builder(this)
                    .setTitle("Update")
                    .setMessage("최신 버전의 앱을 설치 후 재실행 해주시기 바랍니다.")
                    .setCancelable(false)
                    .setPositiveButton(
                        "종료"
                    ) { _, _ -> // Apply SAM
                        this.finish()
                    }.show()
            } else {
                if(PreferenceUtil(binding.root.context).getString("adultCheck","") == "true") {
                    startActivity(Intent(this, FirstTimeActivity::class.java))
                }else{
                    startActivity(Intent(this,WelcomeMainActivity::class.java))
                }
                this.finish()
            }
        }
    }

    private fun dialogDisplayDownServer() {
        AlertDialog.Builder(this)
            .setTitle("Maintenance")
            .setMessage("서버 점검중입니다. \n PM 06:00 ~ PM 08:00")
            .setCancelable(false)
            .setPositiveButton(
                "종료"
            ) { _, _ -> // Apply SAM
                this.finishAffinity()
            }.show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Config.FLAG_WELCOME_REQUEST_PERMISSION) {
            if (isPermitted(applicationContext, Config.WELCOME_REQUEST_PERMISSION)) {
                firebaseRemoteFetch()
            } else {
                Toast.makeText(applicationContext, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
}
