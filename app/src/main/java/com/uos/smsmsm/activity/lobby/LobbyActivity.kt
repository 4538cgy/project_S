package com.uos.smsmsm.activity.lobby

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.content.AddContentActivity
import com.uos.smsmsm.data.TestDTO
import com.uos.smsmsm.databinding.ActivityLobbyBinding
import com.uos.smsmsm.fragment.tabmenu.chatroom.ChatRoomFragment
import com.uos.smsmsm.fragment.tabmenu.friendslist.FriendsListFragment
import com.uos.smsmsm.fragment.tabmenu.other.OtherMenuFragment
import com.uos.smsmsm.fragment.tabmenu.timeline.TimeLineFragment
import com.uos.smsmsm.fragment.tabmenu.userfragment.UserFragment
import com.uos.smsmsm.util.MediaType
import com.uos.smsmsm.util.time.TimeUtil
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LobbyActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityLobbyBinding

    private val viewmodel : SNSUtilViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lobby)
        binding.lifecycleOwner = this
        binding.snsviewmodel = viewmodel
        //캡처 방지
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)


        // 바텀 네비게이션 리스너 초기화
        binding.activityLobbyBottomNavigation.setOnNavigationItemSelectedListener(this)
        // 외부 저장소 권한 읽기
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        binding.activityLobbyBottomNavigation.selectedItemId = R.id.action_friendslist

        // registerPushToken()
    }

    fun registerPushToken() {
        // Fix Deprecated; It`s need some test.
        /*FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mapOf("pushToken" to token!!)
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }*/

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            val token = task.result.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mapOf("pushToken" to token) // Apply extension
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }

    // Change return with When
    override fun onNavigationItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_timeline -> {
            // var -> val
            val timelineFragment = TimeLineFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_lobby_fragmelayout, timelineFragment).commit()
            true
        }
        R.id.action_chat -> {
            // var -> val
            val chatFragment = ChatRoomFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_lobby_fragmelayout, chatFragment).commit()
            true
        }
        R.id.action_friendslist -> {
            // var -> val
            val friendslistFragment = FriendsListFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_lobby_fragmelayout, friendslistFragment).commit()
            true
        }
        R.id.action_more -> {
            // var -> val
            val otherMenuFragment = OtherMenuFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_lobby_fragmelayout, otherMenuFragment).commit()
            true
        }
        else -> false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {
            // System.out.println -> println
            println("데이터 전달 성공적으로 완수1234")
        }

        if (resultCode == 1555) {
            // System.out.println -> println
            println("데이터 전달 성공적으로 완수1666")
        }

        if (requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {

            // var -> val [timestamp, imageFileName, imageUri, storageRef, images]
            val timestamp = TimeUtil().getTime()
            val imageFileName = "TEST_IMAGE_" + timestamp + "_.png"

            val imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            val storageRef =
                FirebaseStorage.getInstance().reference.child("TestImage").child(imageFileName)
            storageRef.putFile(imageUri!!).continueWithTask {
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                val images = TestDTO()
                images.imageUrl = uri.toString()
                images.timestamp = System.currentTimeMillis()
                FirebaseFirestore.getInstance().collection("TextImage").document().set(images)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            startActivity(
                Intent(binding.root.context, AddContentActivity::class.java).apply {
                    putExtra("uri", result.uri)
                    putExtra("mediaType", MediaType.Picture)
                })
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        AlertDialog.Builder(binding.root.context).apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("아니오", null)
            setNegativeButton(
                "예"
            ) { _, _ -> finishAffinity() } // Apply SAM
            setTitle("안내")
        }.show()
    }
}
