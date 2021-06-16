package com.uos.smsmsm.activity.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityInputPhoneNumberBinding
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class InputPhoneNumberActivity : BaseActivity<ActivityInputPhoneNumberBinding>(R.layout.activity_input_phone_number) {

    private lateinit var photoUri : String
    private lateinit var signupType : String

    var progressDialogPhoneAuth: ProgressDialogPhoneAuthLoading? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            activityinputphonenumber = this@InputPhoneNumberActivity

            activityInputPhoneNumberButtonComplete.isEnabled = false

            activityInputPhoneNumberEdittext.apply {

                addTextChangedListener {
                    PhoneNumberFormattingTextWatcher()
                    if (it!!.length > 10){
                        visibleUi()
                    }else{
                        visibleUi()
                    }
                }


            }

            signupType = intent.getStringExtra("signUpType")

        }

        photoUri = intent.getStringExtra("photoUri").toString()

    }

    fun visibleUi(){
        if (binding.activityInputPhoneNumberEdittext.text.length > 10){
            binding.activityInputPhoneNumberButtonVerify.visibility = View.VISIBLE
        }else{
            binding.activityInputPhoneNumberButtonVerify.visibility = View.GONE
        }
    }

    fun interactiveUi(){
        binding.activityInputPhoneNumberButtonComplete.isEnabled = true
        binding.activityInputPhoneNumberEdittext.isEnabled = false
    }

    fun onComplete(view : View){
        var intent = Intent(binding.root.context,InputNickNameActivity::class.java)
        intent.apply {
            putExtra("photoUri",photoUri)
            putExtra("phonenumber",binding.activityInputPhoneNumberEdittext.text.toString())
            startActivity(intent)
        }
    }

    fun signinPhone(view : View) {
            progressDialog.show()
            println("핸드폰 자동 인증 시작")
            val phoneNumber = "+82" + binding.activityInputPhoneNumberEdittext.text.toString()
            var code: String? = null
            FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
                phoneNumber,
                code
            )
            val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        // 성공시
                        Log.d("credential", p0.toString())
                        Log.d("성공", "인증에 성공 했습니다.")
                        Toast.makeText(
                            binding.root.context,
                            "핸드폰 번호 인증에 성공했습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                        progressDialog.dismiss()

                        interactiveUi()

                        if (signupType == "phone") {
                            FirebaseAuth.getInstance().signInWithCredential(p0)
                                .addOnCompleteListener {

                                    Toast.makeText(
                                        binding.root.context,
                                        "회원가입 성공",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }
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

                        progressDialog.dismiss()
                    }

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(p0, p1)
                        Log.d("코드가 전송됨", p0.toString())
                    }

                    override fun onCodeAutoRetrievalTimeOut(p0: String) {
                        super.onCodeAutoRetrievalTimeOut(p0)
                        progressDialog.dismiss()
                    }
                })          // OnVerificationStateChangedCallbacks
                .build()
            // auth.useAppLanguage()
            PhoneAuthProvider.verifyPhoneNumber(options)

    }
}