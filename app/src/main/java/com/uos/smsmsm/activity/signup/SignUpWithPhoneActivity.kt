package com.uos.smsmsm.activity.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.activity.policy.PolicyActivity
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.ActivitySignUpWithPhoneBinding
import com.uos.smsmsm.fragment.tabmenu.userfragment.UserFragment
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.time.TimeUtil
import java.util.concurrent.TimeUnit

// SignUpActivity랑 무슨 차이인지 모르겠어서 일단 원본 유지
class SignUpWithPhoneActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpWithPhoneBinding

    var progressDialogPhoneVerify: ProgressDialogPhoneAuthLoading? = null

    var imageUri: Uri? = null
    var mAuth = FirebaseAuth.getInstance()
    var phoneVerify: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_with_phone)
        binding.signupwithphone = this@SignUpWithPhoneActivity

        // 로딩 초기화
        progressDialogPhoneVerify = ProgressDialogPhoneAuthLoading(binding.root.context)

        // 프로그레스 투명하게
        progressDialogPhoneVerify!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 프로그레스 꺼짐 방지
        progressDialogPhoneVerify!!.setCancelable(false)

        // 인증 요청
        binding.activitySignUpWithPhoneButtonPhoneAuth.setOnClickListener {
            AutoRecieveThePhoneVerifyCode()
        }

        // 프로필 이미지 등록
        binding.activitySignUpWithPhoneCircleimageviewProfile.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, UserFragment.PICK_PROFILE_FROM_ALBUM)
        }

        // 이용약관 클릭시
        binding.activitySignUpWithPhoneLinearCheckboxUsePolicy.setOnClickListener {
            if (!binding.activitySignUpWithPhoneCheckboxUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1001
                )
        }
        // 이용약관 클릭시 2
        binding.activitySignUpWithPhoneCheckboxUsePolicy.setOnClickListener {
            if (!binding.activitySignUpWithPhoneCheckboxUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1001
                )
        }

        // 개인정보 클릭시
        binding.activitySignUpWithPhoneLinearCheckboxUserInfoUsePolicy.setOnClickListener {
            if (!binding.activitySignUpWithPhoneCheckboxUserInfoUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1002
                )
        }
        // 개인정보 클릭시2
        binding.activitySignUpWithPhoneCheckboxUserInfoUsePolicy.setOnClickListener {
            if (!binding.activitySignUpWithPhoneCheckboxUserInfoUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1002
                )
        }

        // 회원 가입 진행
        binding.activitySignUpWithPhoneButtonComplete.setOnClickListener {
            if (binding.activitySignUpWithPhoneCheckboxUsePolicy.isChecked && binding.activitySignUpWithPhoneCheckboxUserInfoUsePolicy.isChecked) {
                if (imageUri != null) {

                    if (phoneVerify) {
                        if (binding.activitySignUpWithPhoneEdittextNickname.length() in 2..7) {
                            AutoRecieveThePhoneVerifyCode()
                        } else {
                            Toast.makeText(
                                binding.root.context,
                                "닉네임은 2자 이상 8자 미만으로 설정해주세요.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "핸드폰 번호 인증을 진행해주세요.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(binding.root.context, "프로필 사진을 등록해주세요.", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(
                    binding.root.context,
                    "이용약관과 개인정보처리 방침을 동의하지 않으면 회원가입이 불가능합니다.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // 핸드폰 자동인증 처리
    private fun AutoRecieveThePhoneVerifyCode() {

        progressDialogPhoneVerify?.show()
        println("핸드폰 자동 인증 시작")
        val phoneNumber = "+82" + binding.activitySignUpWithPhoneEdittextPhone.text.toString()
        var code: String? = null
        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
            phoneNumber,
            code
        )

        // auth.useAppLanguage()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            120,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    // 성공시
                    Log.d("credential", p0.toString())
                    Log.d("성공", "인증에 성공 했습니다.")
                    Toast.makeText(
                        binding.root.context,
                        "핸드폰 번호 인증에 성공했습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    progressDialogPhoneVerify?.dismiss()

                    FirebaseAuth.getInstance().signInWithCredential(p0).addOnCompleteListener {

                        Toast.makeText(binding.root.context, "회원가입 성공", Toast.LENGTH_LONG).show()

                        // 회원 가입 성공시 회원 정보 저장
                        uploadPhoto()
                    }.addOnFailureListener {
                        Toast.makeText(binding.root.context, "회원가입 실패", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    // 실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(
                        binding.root.context,
                        "핸드폰 인증에 실패했습니다. \n 올바른 번호를 입력해주세요.",
                        Toast.LENGTH_LONG
                    ).show()

                    progressDialogPhoneVerify?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialogPhoneVerify?.dismiss()
                }
            }
        )
    }

    fun uploadPhoto() {
        println("프로필 사진 업로드 시작")
        // 사진 저장
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var storageRef =
            FirebaseStorage.getInstance().reference.child("userProfileImages")
                .child(uid!!)
        storageRef.putFile(imageUri!!)
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
        userDTO.userName = binding.activitySignUpWithPhoneEdittextNickname.text.toString()
        // 유저 폰번호
        userDTO.phoneNumber = binding.activitySignUpWithPhoneEdittextPhone.text.toString()
        // 가입 시간 [핸드폰기준]
        userDTO.timeStamp = System.currentTimeMillis()

        // 유저 최초 포인트
        userDTO.point = 100
        userDTO.uid = mAuth.currentUser?.uid.toString()
        userDTO.memberRating = 0
        userDTO.policyAccept = true

        FirebaseFirestore.getInstance().collection("test").document("testUser")
            .collection("userInfo")
            .document("userAccount").collection(mAuth.currentUser?.uid.toString())
            .document("accountInfo").set(userDTO)
            .addOnSuccessListener {
                startActivity(Intent(binding.root.context, LobbyActivity::class.java))
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                UserFragment.PICK_PROFILE_FROM_ALBUM -> {

                    binding.activitySignUpWithPhoneCircleimageviewProfile.setImageURI(data?.data)
                    imageUri = data?.data

                    if (mAuth.currentUser != null) {
                        binding.activitySignUpWithPhoneCircleimageviewProfile.setImageURI(data?.data)
                        imageUri = data?.data
                    }
                }

                1001 -> {
                    if (resultCode == RESULT_OK)
                        binding.activitySignUpWithPhoneCheckboxUsePolicy.isChecked = true
                }

                1002 -> {
                    if (resultCode == RESULT_OK)
                        binding.activitySignUpWithPhoneCheckboxUserInfoUsePolicy.isChecked = true
                }
            }
        }
    }
}
