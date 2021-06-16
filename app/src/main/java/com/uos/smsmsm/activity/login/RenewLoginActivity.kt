package com.uos.smsmsm.activity.login

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.activity.signup.SignUpActivity
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.databinding.ActivityLoginRenewBinding
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.extensions.toast
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class RenewLoginActivity : BaseActivity<ActivityLoginRenewBinding>(R.layout.activity_login_renew) {

    private var auth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient? = null
    private val GOOGLE_LOGIN_CODE = 9001
    var progressDialogPhoneAuth: ProgressDialogPhoneAuthLoading? = null
    private val SNSViewMdodel : ContentUtilViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.apply {
            activityloginrenew = this@RenewLoginActivity
            snsviewmodel = SNSViewMdodel


            activityLoginRenewTextviewGoToSignup.setOnClickListener {
                startActivity(Intent(root.context,FirstTimeActivity::class.java))
                finish()
            }
        }

        SNSViewMdodel.contentEdittext.observe(this, Observer {

            println("번호 $it" )
            if(it.contains("-")){
                toast("' - ' 를 제외하고 입력해주세요.")
            }

            if (it.length > 10){
                visiblePhoneLoginButton(true)
            }else{
                visiblePhoneLoginButton(false)
            }
        })
    }

    fun visiblePhoneLoginButton(state : Boolean){
        if (state) {
            binding.activityLoginRenewButtonPhoneVerify.visibility = View.VISIBLE
        }else{
            binding.activityLoginRenewButtonPhoneVerify.visibility = View.GONE
        }
    }

    fun google(view : View){
        progressDialog.show()
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun phoneVerifyAndLogin(view : View){

            // 핸드폰 자동인증 처리

            val phoneNumber = "+82" + binding.activityLoginRenewEdittextPhonenumber.text.toString()
            var code: String? = null
            FirebaseAuth.getInstance().firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(
                phoneNumber,
                code
            )

            // auth.useAppLanguage()

            binding.activityLoginRenewEdittextPhonenumber.isEnabled = false
            binding.activityLoginRenewButtonPhoneVerify.isEnabled = false

            progressDialogPhoneAuth?.show()

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
                                binding.activityLoginRenewEdittextPhonenumber.isEnabled = true
                                binding.activityLoginRenewButtonPhoneVerify.isEnabled = true
                            }.addOnCompleteListener {
                                moveMainPage(it.result?.user)
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

    fun phone(view : View){
        binding.activityLoginRenewEdittextPhonenumber.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            // var -> val [result, account]
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    // second step
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    // Make private
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        // var -> val
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task -> // Remove safe-call
            if (task.isSuccessful) {
                // login
                moveMainPage(task.result?.user)
            } else {
                // show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {

            FirebaseFirestore.getInstance().collection("User").document("UserData")
                .collection("userInfo").whereEqualTo("uid",auth.currentUser?.uid.toString())
                .addSnapshotListener { documentSnapshot, _ -> // Remove useless parameter
                    if (documentSnapshot != null) {
                        if (!documentSnapshot.isEmpty) { // Remove NonNull
                            startActivity(Intent(this, LobbyActivity::class.java))
                        } else {
                            startActivity(Intent(this, SignUpActivity::class.java))
                        }
                        finish()
                    }
                }
        }
    }
}