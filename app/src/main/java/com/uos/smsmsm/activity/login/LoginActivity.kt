package com.uos.smsmsm.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
import com.uos.smsmsm.activity.signup.SignUpWithPhoneActivity
import com.uos.smsmsm.databinding.ActivityLoginBinding
import com.uos.smsmsm.util.ProgressDialogPhoneAuthLoading
import com.uos.smsmsm.util.SharedData
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    var auth = FirebaseAuth.getInstance()
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var progressDialogPhoneAuth : ProgressDialogPhoneAuthLoading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.activitylogin = this@LoginActivity

        //구글 로그인 옵션 활성화
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        //폰번호 로그인
        binding.activityLoginButtonLoginWithPhonenumber.setOnClickListener {
            signinPhone()
        }

        //구글 로그인
        binding.activityLoginButtonGoogle.setOnClickListener {
            googleLogin()
        }

        //회원 가입으로 이동
        binding.activityLoginTextviewSignup.setOnClickListener {
            startActivity(Intent(binding.root.context,SignUpWithPhoneActivity::class.java))
        }

    }


    //자동 로그인 체크
    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    //구글 로그인 시작
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if (result.isSuccess) {
                    var account = result.signInAccount
                    //second step
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //login
                moveMainPage(task.result?.user)
            } else {
                //show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signinPhone(){
        //핸드폰 자동인증 처리

        val phoneNumber = "+82" + binding.activityLoginEdittextPhonenumber.text.toString()
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
                        "핸드폰 인증에 성공했습니다.",
                        Toast.LENGTH_LONG).show()
                    progressDialogPhoneAuth?.dismiss()

                    FirebaseAuth.getInstance().signInWithCredential(p0).addOnFailureListener {


                    }.addOnCompleteListener {
                        //startActivity(Intent(binding.root.context,LobbyActivity::class.java))
                        //finish()


                    }


                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 실패했습니다..",
                        Toast.LENGTH_LONG).show()





                    progressDialogPhoneAuth?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialogPhoneAuth?.dismiss()



                }

            }
        )


    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {

            FirebaseFirestore.getInstance().collection("test").document("testUser").collection("userInfo").document("userAccount").collection(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .document("accountInfo")
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot!=null) {
                        if (documentSnapshot!!.exists()) {
                            startActivity(Intent(this, LobbyActivity::class.java))
                        } else{
                            startActivity(Intent(this, SignUpActivity::class.java))
                        }
                        finish()
                    }

                }


        }
    }




}