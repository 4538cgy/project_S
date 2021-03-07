package com.uos.smsmsm.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.activity.signup.SignUpActivity
import com.uos.smsmsm.activity.signup.SignUpWithPhoneActivity
import com.uos.smsmsm.databinding.ActivityLoginBinding
import com.uos.smsmsm.util.dialog.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.extensions.toast
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    // Make private [auth, googleSignInClient, GOOGLE_LOGIN_CODE]
    private var auth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient? = null
    private val GOOGLE_LOGIN_CODE = 9001
    var progressDialogPhoneAuth: ProgressDialogPhoneAuthLoading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.activitylogin = this@LoginActivity

        // 구글 로그인 옵션 활성화
        // var -> val
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 폰번호 로그인
        binding.activityLoginButtonLoginWithPhonenumber.setOnClickListener {
            signinPhone()
        }

        // 구글 로그인
        binding.activityLoginButtonGoogle.setOnClickListener {
            googleLogin()
        }

        // 회원 가입으로 이동
        binding.activityLoginTextviewSignup.setOnClickListener {
            startActivity(Intent(binding.root.context, SignUpWithPhoneActivity::class.java))
        }
    }

    // 자동 로그인 체크
    override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser) // Remove safe-call
    }

    // 구글 로그인 시작
    // Make private
    private fun googleLogin() {
        // var -> val
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
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

    // Make private
    private fun signinPhone() {
        // 핸드폰 자동인증 처리

        val phoneNumber = "+82" + binding.activityLoginEdittextPhonenumber.text.toString()
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
                            // startActivity(Intent(binding.root.context,LobbyActivity::class.java))
                            // finish()
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

    // Make private
    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {

            FirebaseFirestore.getInstance().collection("test").document("testUser")
                .collection("userInfo").document("userAccount")
                .collection(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .document("accountInfo")
                .addSnapshotListener { documentSnapshot, _ -> // Remove useless parameter
                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) { // Remove NonNull
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
