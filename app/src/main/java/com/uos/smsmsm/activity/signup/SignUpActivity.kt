package com.uos.smsmsm.activity.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.uos.smsmsm.databinding.ActivitySignUpBinding
import com.uos.smsmsm.fragment.tabmenu.userfragment.UserFragment
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.time.TimeUtil
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    var mAuth: FirebaseAuth? = null
    var imageUri: Uri? = null
    var progressDialog: ProgressDialogPhoneAuthLoading? = null
    var phoneVerify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.signup = this@SignUpActivity


        //로딩 초기화
        progressDialog = ProgressDialogPhoneAuthLoading(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

        //파이어베이스 auth 초기화
        mAuth = FirebaseAuth.getInstance()

        //인증 요청
        binding.activitySignUpButtonPhoneAuth.setOnClickListener {
            AutoRecieveThePhoneVerifyCode()
        }

        //프로필 이미지 등록
        binding.activitySignUpCircleimageviewProfile.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, UserFragment.PICK_PROFILE_FROM_ALBUM)
        }

        //이용약관 클릭시
        binding.activitySignUpLinearCheckboxUsePolicy.setOnClickListener {
            if (!binding.activitySignUpCheckboxUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1001
                )
        }
        //이용약관 클릭시 2
        binding.activitySignUpCheckboxUsePolicy.setOnClickListener {
            if (!binding.activitySignUpCheckboxUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1001
                )
        }

        //개인정보 클릭시
        binding.activitySignUpLinearCheckboxUserInfoUsePolicy.setOnClickListener {
            if (!binding.activitySignUpCheckboxUserInfoUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1002
                )
        }
        //개인정보 클릭시2
        binding.activitySignUpCheckboxUserInfoUsePolicy.setOnClickListener {
            if (!binding.activitySignUpCheckboxUserInfoUsePolicy.isChecked)
                startActivityForResult(
                    Intent(binding.root.context, PolicyActivity::class.java),
                    1002
                )
        }


        //회원 가입 진행
        binding.activitySignUpButtonComplete.setOnClickListener {
            if (binding.activitySignUpCheckboxUsePolicy.isChecked && binding.activitySignUpCheckboxUserInfoUsePolicy.isChecked) {
                if (imageUri != null) {


                    if (phoneVerify) {
                        if (binding.activitySignUpEdittextNickname.length() in 2..7) {
                            saveData()
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
                }else{
                    Toast.makeText(binding.root.context,"프로필 사진을 등록해주세요.",Toast.LENGTH_LONG).show()
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

    //핸드폰 자동인증 처리
    private fun AutoRecieveThePhoneVerifyCode() {

        progressDialog?.show()

        val phoneNumber = "+82" + binding.activitySignUpEdittextPhone.text.toString()
        var code: String? = null
        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
            phoneNumber,
            code
        )

        //auth.useAppLanguage()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            120,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    //성공시
                    Log.d("credential", p0.toString())
                    Log.d("성공", "인증에 성공 했습니다.")
                    Toast.makeText(
                        binding.root.context,
                        "핸드폰 인증에 성공했습니다. \n 나머지 정보를 입력해주세요.",
                        Toast.LENGTH_LONG
                    ).show()

                    binding.activitySignUpButtonPhoneAuth.isEnabled = false
                    binding.activitySignUpButtonPhoneAuth.isClickable = false
                    binding.activitySignUpButtonPhoneAuth.isFocusable = false
                    binding.activitySignUpEdittextPhone.isEnabled = false
                    binding.activitySignUpEdittextPhone.isClickable = false
                    binding.activitySignUpEdittextPhone.isFocusable = false

                    progressDialog?.dismiss()


                    phoneVerify = true
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(
                        binding.root.context,
                        "핸드폰 인증에 실패했습니다. \n 올바른 번호를 입력해주세요.",
                        Toast.LENGTH_LONG
                    ).show()





                    progressDialog?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialog?.dismiss()
                    Toast.makeText(binding.root.context, "시간 초과 \n 다시 시도해주세요", Toast.LENGTH_LONG)
                        .show()

                }

            }
        )

    }

    //데이터 저장
    fun saveData() {
        var userDTO = UserDTO()

        //유저 닉네임
        userDTO.userName = binding.activitySignUpEdittextNickname.text.toString()
        //유저 폰번호
        userDTO.phoneNumber = binding.activitySignUpEdittextPhone.text.toString()
        //가입 시간 [핸드폰기준]
        userDTO.timeStamp = System.currentTimeMillis()
        //핸드폰 시간
        userDTO.timeStr = TimeUtil().getTime()
        //유저 최초 포인트
        userDTO.point = 100
        userDTO.uid = mAuth?.currentUser?.uid.toString()
        userDTO.memberRating = 0
        userDTO.policyAccept = true


        FirebaseFirestore.getInstance().collection("test").document("testUser").collection("userInfo")
            .document("userAccount").collection(mAuth?.currentUser?.uid.toString()).document("accountInfo").set(userDTO)
            .addOnSuccessListener {
                startActivity(Intent(binding.root.context, LobbyActivity::class.java))
                finish()
            }




    }

    //사진 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                UserFragment.PICK_PROFILE_FROM_ALBUM -> {

                    if (mAuth?.currentUser != null) {
                        binding.activitySignUpCircleimageviewProfile.setImageURI(data?.data)
                        imageUri = data?.data
                        var uid = FirebaseAuth.getInstance().currentUser?.uid
                        var storageRef =
                            FirebaseStorage.getInstance().reference.child("userProfileImages")
                                .child(uid!!)
                        storageRef.putFile(imageUri!!)
                            .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                                return@continueWithTask storageRef.downloadUrl
                            }.addOnSuccessListener { uri ->
                                var map = HashMap<String, Any>()
                                map["image"] = uri.toString()
                                FirebaseFirestore.getInstance().collection("profileImages")
                                    .document(uid).set(map)


                            }
                    }
                }

                1001 -> {
                    if (resultCode == RESULT_OK)
                        binding.activitySignUpCheckboxUsePolicy.isChecked = true
                }

                1002 -> {
                    if (resultCode == RESULT_OK)
                        binding.activitySignUpCheckboxUserInfoUsePolicy.isChecked = true
                }

            }
        }

    }
}