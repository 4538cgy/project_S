package com.uos.smsmsm.activity.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.uos.smsmsm.activity.policy.PolicyActivity
import com.uos.smsmsm.databinding.ActivitySignUpBinding
import com.uos.smsmsm.fragment.tabmenu.UserFragment
import com.uos.smsmsm.util.ProgressDialogPhoneAuthLoading
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    var mAuth : FirebaseAuth ?= null
    var imageUri : Uri? = null
    var progressDialog : ProgressDialogPhoneAuthLoading ? = null
    var phoneVerify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        binding.signup = this@SignUpActivity


        //로딩 초기화
        progressDialog = ProgressDialogPhoneAuthLoading(binding.root.context)

        //프로그레스 투명하게
        progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //프로그레스 꺼짐 방지
        progressDialog!!.setCancelable(false)

        //파이어베이스 auth 초기화
        mAuth = FirebaseAuth.getInstance()

        //폰번호 얻어오기
        binding.activitySignUpEdittextPhone.text

        //인증 요청
        binding.activitySignUpButtonPhoneAuth.setOnClickListener {
            AutoRecieveThePhoneVerifyCode()
        }

        //프로필 이미지 등록
        binding.activitySignUpCircleimageviewProfile.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,UserFragment.PICK_PROFILE_FROM_ALBUM)
        }

        //이용약관 클릭시
        binding.activitySignUpLinearCheckboxUsePolicy.setOnClickListener {
            if (!binding.activitySignUpCheckboxUsePolicy.isChecked)
            startActivityForResult(Intent(binding.root.context,PolicyActivity::class.java),1001)
        }

        //개인정보 클릭시
        binding.activitySignUpLinearCheckboxUserInfoUsePolicy.setOnClickListener {
            if (!binding.activitySignUpCheckboxUserInfoUsePolicy.isChecked)
            startActivityForResult(Intent(binding.root.context,PolicyActivity::class.java),1002)
        }

        binding.activitySignUpButtonComplete.setOnClickListener {

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
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 성공했습니다. \n 나머지 정보를 입력해주세요.",
                        Toast.LENGTH_LONG).show()
                    progressDialog?.dismiss()


                    phoneVerify = true
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 실패했습니다. \n 올바른 번호를 입력해주세요.",
                        Toast.LENGTH_LONG).show()





                    progressDialog?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialog?.dismiss()
                    Toast.makeText(binding.root.context,"시간 초과 \n 다시 시도해주세요",Toast.LENGTH_LONG).show()
               
                }

            }
        )

    }

    //데이터 저장
    fun saveData(){

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