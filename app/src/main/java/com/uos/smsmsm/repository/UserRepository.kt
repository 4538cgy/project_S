package com.uos.smsmsm.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.FriendsDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val rdb = FirebaseDatabase.getInstance()
    private val authUid = FirebaseAuth.getInstance().currentUser?.uid


    //유저 생성
    @ExperimentalCoroutinesApi
    fun createUser(userDTO : UserDTO) = callbackFlow<Boolean> {
        val databaseReference = db.collection("user").document()
        val eventListener = databaseReference.set(userDTO).addOnCompleteListener {
            this@callbackFlow.sendBlocking(true)
        }.addOnFailureListener {
            this@callbackFlow.sendBlocking(false)
        }
    }

    //테스트 유저 생성 - delete soon
    @ExperimentalCoroutinesApi
    fun createTestUser(userData: UserDTO) = callbackFlow<Boolean> {
        val databaseReference = db.collection("testUser").document()
        val eventListener = databaseReference.set(userData).addOnCompleteListener {
            this@callbackFlow.sendBlocking(true)
        }.addOnFailureListener {
            this@callbackFlow.sendBlocking(false)
        }

        awaitClose { eventListener.isComplete }
    }

    //테스트 유저 목록 가져오기 - delete soon
    @ExperimentalCoroutinesApi
    fun getTestUserList() = callbackFlow<ArrayList<UserDTO>> {
        val databaseReference = db.collection("testUser")
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            var userList = arrayListOf<UserDTO>()

            value?.documents?.forEach {
                userList.add(it.toObject(UserDTO::class.java)!!)
            }
            this@callbackFlow.sendBlocking(userList)
        }

        awaitClose { eventListener.remove() }
    }

    //모든 유저 정보 가져오기
    @ExperimentalCoroutinesApi
    fun getAllUser() = callbackFlow<ArrayList<UserDTO>> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value!!.isEmpty) return@addSnapshotListener
            if (!value!!.isEmpty){
                var user = arrayListOf<UserDTO>()

                user.addAll(value.toObjects(UserDTO::class.java))


                this@callbackFlow.sendBlocking(user)
            }
        }

        awaitClose { eventListener }
    }

    //유저의 프로필 이미지 url 가져오기
    @ExperimentalCoroutinesApi
    fun getUserProfileImage(uid : String) = callbackFlow<String> {
        val databaseReference = db.collection("profileImages").document(uid)
        val eventListener = databaseReference.get()
        eventListener.addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                var url : String = task.result!!["image"].toString()
                this@callbackFlow.sendBlocking(url)
            }
        }
        awaitClose { eventListener }

    }

    //유저 닉네임 가져오기
    @ExperimentalCoroutinesApi
    fun getUserNickName(uid : String) = callbackFlow<String> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo").whereEqualTo("uid",uid)
        val eventListener = databaseReference.get()
        eventListener.addOnCompleteListener {
            if (it.isSuccessful){
                var nickName : String = "미확인 사용자"
                println("유저 닉네임 가져오기 repo 엌 ${it.toString()}")
                it.result.documents.forEach {
                    if (it["uid"]!! == uid){
                        nickName = it["userName"].toString()
                    }
                }
                this@callbackFlow.sendBlocking(nickName)
            }
        }

        awaitClose { eventListener }
    }

    //특정 유저 한명의 정보 가져오기
    @ExperimentalCoroutinesApi
    fun getUser(uid : String) = callbackFlow<UserDTO> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo").whereEqualTo("uid",uid)
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value != null){
                if (value.isEmpty)return@addSnapshotListener
                if(!value.isEmpty)
                {
                    var user = UserDTO()
                    value.documents.forEach {
                        if (it["uid"]!! == uid)
                        {
                            user = it.toObject(UserDTO::class.java)!!
                        }
                    }
                    this@callbackFlow.sendBlocking(user)
                }
            }
        }
        awaitClose { eventListener.remove() }
    }

    //유저의 닉네임이 존재하는지 체크
    @ExperimentalCoroutinesApi
    fun checkUserNickName(userNickName: String) = callbackFlow<Boolean> {
        val databaseReference = db.collection("user").document("userNickNameList")
        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful)
            {
                if (it.result != null){
                    var userList : ArrayList<String> = arrayListOf()
                    if (userList.contains(userNickName))
                        this@callbackFlow.sendBlocking(true)
                    else
                        this@callbackFlow.sendBlocking(false)
                }
            }
        }
    }

    //uid 의 친구 목록에 destinationUid 가 있는지 확인
    @ExperimentalCoroutinesApi
    fun isFriend(uid : String, destinationUid :String) = callbackFlow<Boolean> {

        val databaseReference = db.collection("User")
            .document("UserData")
            .collection("userInfo")
            .whereEqualTo("uid", uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)){
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(it.id)
                                .collection("FriendsList")
                                .whereEqualTo("uid",destinationUid)

                            val eventListener2 = databaseReference2.get().addOnCompleteListener {
                                if (it.isSuccessful){
                                    if (it.result.documents != null){
                                        this@callbackFlow.sendBlocking(true)
                                    }else{
                                        this@callbackFlow.sendBlocking(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        awaitClose { eventListener }
    }

    //친구 추가
    @ExperimentalCoroutinesApi
    fun addFriend(uid: String, destinationUid: String, friendsDTO: FriendsDTO ) = callbackFlow<Boolean> {

        val databaseReference = db.collection("User")
            .document("UserData")
            .collection("userInfo")
            .whereEqualTo("uid", uid)
        val eventListener = databaseReference.get().addOnCompleteListener {
            if(it.isSuccessful){
                if (it.result != null){
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)){
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(it.id)
                                .collection("FriendsList")
                                .document(uid)

                            val eventListener2 = databaseReference2.set(friendsDTO).addOnCompleteListener {
                                if (it.isSuccessful){
                                    this@callbackFlow.sendBlocking(true)
                                }else{
                                    this@callbackFlow.sendBlocking(false)
                                }
                            }.addOnFailureListener {
                                this@callbackFlow.sendBlocking(false)
                                println("친구 추가 실패 ${it.toString()}")
                            }
                        }
                    }
                }
            }
        }
        awaitClose { eventListener }
    }

    //친구 목록 가져오기
    fun getFriendsList(uid : String) = callbackFlow<FriendsDTO> {
        val databaseReference = db.collection("User")
            .document("UserData")
            .collection("userInfo")
            .whereEqualTo("uid",uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful){
                if (it.result != null){
                    it.result.documents.forEach {
                        if(it["uid"]!!.equals(uid)){
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(it.id)
                                .collection("FriendsList")
                                .document(uid)

                            val eventListener2 = databaseReference2.addSnapshotListener { value, error ->
                                if (value != null){
                                    if(value.exists()){
                                        this@callbackFlow.sendBlocking(value.toObject(FriendsDTO::class.java)!!)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener {
            println("친구목록 불러오기 실패")
        }
        awaitClose { eventListener }
    }


}