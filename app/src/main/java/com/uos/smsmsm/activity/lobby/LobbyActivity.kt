package com.uos.smsmsm.activity.lobby

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.smsmsm.R
import com.uos.smsmsm.data.TestDTO
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.ActivityLobbyBinding
import com.uos.smsmsm.fragment.tabmenu.chatroom.ChatRoomFragment
import com.uos.smsmsm.fragment.tabmenu.friendslist.FriendsListFragment
import com.uos.smsmsm.fragment.tabmenu.other.OtherMenuFragment
import com.uos.smsmsm.fragment.tabmenu.timeline.TimeLineFragment
import com.uos.smsmsm.fragment.tabmenu.userfragment.UserFragment
import com.uos.smsmsm.util.time.TimeUtil

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
            R.id.action_home -> {
                var timelineFragment = TimeLineFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_lobby_fragmelayout, timelineFragment).commit()
                return true
            }
            R.id.action_search -> {
                var chatFragment = ChatRoomFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_lobby_fragmelayout, chatFragment).commit()
                return true
            }
            R.id.action_favorite_alarm -> {
                var friendslistFragment = FriendsListFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_lobby_fragmelayout, friendslistFragment).commit()
                return true
            }
            R.id.action_photo ->{
                var otherMenuFragment = OtherMenuFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_lobby_fragmelayout, otherMenuFragment).commit()
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1234){
            System.out.println("데이터 전달 성공적으로 완수1234")
        }

        if(resultCode == 1555)
            System.out.println("데이터 전달 성공적으로 완수1666")


        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){

            var timestamp = TimeUtil().getTime()
            var imageFileName = "TEST_IMAGE_" + timestamp + "_.png"

            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("TestImage").child(imageFileName)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->

                var images = TestDTO()
                images.imageUrl = uri.toString()
                images.timestamp = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("TextImage").document().set(images)


            }
        }
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