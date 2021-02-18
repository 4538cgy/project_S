package com.uos.smsmsm.activity.lobby

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityLobbyBinding
import com.uos.smsmsm.fragment.tabmenu.UserFragment

class LobbyActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lobby)
        binding.lobby = this@LobbyActivity

        //바텀 네비게이션 리스너 초기화
        binding.activityLobbyBottomNavigation.setOnNavigationItemSelectedListener(this)
        //외부 저장소 권한 읽기
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        binding.activityLobbyBottomNavigation.selectedItemId = R.id.action_home

        //registerPushToken()
    }

    override fun onStop() {
        super.onStop()
    }

    fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            val token = task.result?.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_account -> {
                var userFragment = UserFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_lobby_fragmelayout, userFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        var builder = AlertDialog.Builder(binding.root.context)


        builder.apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                return@OnClickListener
            })
            setNegativeButton("예", DialogInterface.OnClickListener { dialog, which ->
                finishAffinity()
            })
            setTitle("안내")
            show()
        }
    }
}