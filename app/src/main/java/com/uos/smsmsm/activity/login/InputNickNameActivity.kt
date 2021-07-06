package com.uos.smsmsm.activity.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.ActivityInputNickNameBinding
import com.uos.smsmsm.util.OneClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InputNickNameActivity : BaseActivity<ActivityInputNickNameBinding>(R.layout.activity_input_nick_name) {

    lateinit var phoneNumber : String
    lateinit var photoUri : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityinputnickname = this@InputNickNameActivity

            activityInputNickNameEdittext.addTextChangedListener {
                textLenghtInteractive(it!!.length)
            }
            activityInputNickNameButtonComplete.setOnClickListener(object: OneClickListener(){
                override fun onOneClick(v: View?) {
                    // 연타시 연속으로 동작하게 되어 원클릭으로 기능 변경
                    uploadPhoto()
                }

            })
        }

        phoneNumber = intent.getStringExtra("phonenumber")
        photoUri = intent.getStringExtra("photoUri")

    }

    fun textLenghtInteractive(lenght : Int){
        binding.activityInputNickNameTextviewNicknameSize.text = "$lenght/12"

        binding.activityInputNickNameButtonComplete.isEnabled = lenght > 2
    }
    
    fun onComplete(view : View)
    {
        uploadPhoto()
    }

    fun uploadPhoto() {
        println("프로필 사진 업로드 시작")
        // 사진 저장
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var storageRef =
            FirebaseStorage.getInstance().reference.child("userProfileImages")
                .child(uid!!)
        storageRef.putFile(Uri.parse(photoUri))
            .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                println("프로필 사진 업로드 성공")
                var map = HashMap<String, Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages")
                    .document(uid).set(map)

                // 사진 저장 완료 후 DB에 유저 정보 저장
                userDataSave()
            }
    }

    fun userDataSave() {
        println("DB에 회원정보 저장 시작")
        var userDTO = UserDTO()

        // 회원정보 DB에 저장

        // 유저 닉네임
        userDTO.userName = binding.activityInputNickNameEdittext.text.toString()
        // 유저 폰번호
        userDTO.phoneNumber = phoneNumber
        // 가입 시간 [핸드폰기준]
        userDTO.timeStamp = System.currentTimeMillis()

        // 유저 최초 포인트
        userDTO.point = 100
        userDTO.uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        userDTO.memberRating = 0
        userDTO.policyAccept = true

        FirebaseFirestore.getInstance().collection("User").document("UserData")
            .collection("userInfo").document().set(userDTO)
            .addOnSuccessListener {
                startActivity(Intent(binding.root.context, LobbyActivity::class.java))
                finish()
            }
    }
}