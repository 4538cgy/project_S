package com.uos.smsmsm.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    val model : LoginViewModel by viewModels()
    var auth = FirebaseAuth.getInstance()
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.activitylogin = model

        //구글 로그인 옵션 활성화
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

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
                    progressDialog?.dismiss()

                    FirebaseAuth.getInstance().signInWithCredential(p0).addOnFailureListener {

                        var log = PhoneAuthLog()
                        log.log = p0.toString()
                        log.serverTimestamp = System.currentTimeMillis()
                        log.uid = binding.emailEdittext.text.toString()
                        log.timestamp = TimeUtil().getTime()
                        FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("LoginLog").document().set(log)
                            .addOnFailureListener {
                                println("로그 저장 실패"+ it.toString())
                            }.addOnCompleteListener {
                                println("로그 저장 성공"+ it.toString())
                            }
                    }.addOnCompleteListener {
                        startActivity(Intent(binding.root.context,LobbyActivity::class.java))
                        finish()

                        var log = PhoneAuthLog()
                        log.log = p0.toString()
                        log.serverTimestamp = System.currentTimeMillis()
                        log.uid = binding.emailEdittext.text.toString()
                        log.timestamp = TimeUtil().getTime()
                        FirebaseFirestore.getInstance().collection("Log").document("SuccessLog").collection("LoginLog").document().set(log)
                            .addOnFailureListener {
                                println("로그 저장 실패"+ it.toString())
                            }.addOnCompleteListener {
                                println("로그 저장 성공"+ it.toString())
                            }
                    }


                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    //실패시
                    Log.d("exception", p0.toString())
                    Log.d("실패", "인증에 실패 했습니다.")
                    Toast.makeText(binding.root.context,
                        "핸드폰 인증에 실패했습니다..",
                        Toast.LENGTH_LONG).show()

                    var log = PhoneAuthLog()
                    log.log = p0.toString()
                    log.serverTimestamp = System.currentTimeMillis()
                    log.uid = binding.emailEdittext.text.toString()
                    log.timestamp = TimeUtil().getTime()

                    FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                        .addOnFailureListener {
                            println("로그 저장 실패"+ it.toString())
                        }.addOnCompleteListener {
                            println("로그 저장 성공"+ it.toString())
                        }



                    progressDialog?.dismiss()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("코드가 전송됨", p0.toString())
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    progressDialog?.dismiss()

                    var log = PhoneAuthLog()
                    log.log = p0.toString()
                    log.serverTimestamp = System.currentTimeMillis()
                    log.uid = binding.emailEdittext.text.toString()
                    log.timestamp = TimeUtil().getTime()

                    FirebaseFirestore.getInstance().collection("Log").document("FailLog").collection("PhoneAuthLog").document().set(log)
                        .addOnFailureListener {
                            println("로그 저장 실패"+ it.toString())
                        }.addOnCompleteListener {
                            println("로그 저장 성공"+ it.toString())
                        }

                }

            }
        )


    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {

            FirebaseFirestore.getInstance().collection("userInfo").document("userData").collection(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .document("accountInfo")
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->


                    if (documentSnapshot!=null) {
                        if (documentSnapshot!!.exists()) {
                            SharedData.prefs.setString("userInfo", "yes")
                        } else {
                            SharedData.prefs.setString("userInfo", "no")
                        }

                        if (SharedData.prefs.getString("userInfo", "no").equals("yes")) {
                            SharedData.prefs.setString("emailVerify","yes")
                            startActivity(Intent(this, LobbyActivity::class.java))

                        } else {
                            SharedData.prefs.setString("emailVerify","yes")
                            startActivity(Intent(this, SignUpActivity::class.java))
                        }
                        finish()
                    }

                }


        }
    }


}