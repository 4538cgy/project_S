package com.uos.smsmsm.activity.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityInputPhoneNumberBinding
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.extensions.toast
import java.util.concurrent.TimeUnit

class InputPhoneNumberActivity : BaseActivity<ActivityInputPhoneNumberBinding>(R.layout.activity_input_phone_number) {

    private lateinit var photoUri : String

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
    }

    fun onComplete(view : View){
        var intent = Intent(binding.root.context,InputNickNameActivity::class.java)
        intent.apply {
            putExtra("photoUri",photoUri)
            startActivity(intent)
        }
    }

    fun signinPhone(view : View) {
        // 핸드폰 자동인증 처리

        val phoneNumber = "+82" + binding.activityInputPhoneNumberEdittext.text.toString()
        var code: String? = null
        FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
            phoneNumber,
            code
        )

        // auth.useAppLanguage()

        // Fix Deprecated
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setActivity(this)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        // 성공시
                        Log.d("credential", p0.toString())
                        Log.d("성공", "인증에 성공 했습니다.")
                        // Apply extension
                        toast("핸드폰 인증에 성공했습니다.")
                        progressDialogPhoneAuth?.dismiss()

                        FirebaseAuth.getInstance().signInWithCredential(p0).addOnFailureListener {
                        }.addOnCompleteListener {
                            interactiveUi()
                            startActivity(Intent(binding.root.context,InputNickNameActivity::class.java))
                            finish()
                        }
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        // 실패시
                        Log.d("exception", p0.toString())
                        Log.d("실패", "인증에 실패 했습니다.")
                        // Apply extension
                        toast("핸드폰 인증에 실패했습니다..")

                        progressDialogPhoneAuth?.dismiss()
                    }

                    override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(p0, p1)
                        // Revmoe `String.toString()`
                        Log.d("코드가 전송됨", p0)
                    }

                    override fun onCodeAutoRetrievalTimeOut(p0: String) {
                        super.onCodeAutoRetrievalTimeOut(p0)
                        progressDialogPhoneAuth?.dismiss()
                    }
                }
                ).build()
        )
    }
}