package com.uos.smsmsm.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val rdb = FirebaseDatabase.getInstance()
    private val authUid = FirebaseAuth.getInstance().currentUser?.uid

    @ExperimentalCoroutinesApi
    fun createUser(userDTO : UserDTO) = callbackFlow<Boolean> {
        val databaseReference = db.collection("user").document()
        val eventListener = databaseReference.set(userDTO).addOnCompleteListener {
            this@callbackFlow.sendBlocking(true)
        }.addOnFailureListener {
            this@callbackFlow.sendBlocking(false)
        }
    }

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
    @ExperimentalCoroutinesApi
    fun getUser(uid : String) = callbackFlow<ArrayList<UserDTO>> {
        val databaseReference = db.collection("user").whereEqualTo("uid",uid)
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value != null){
                if (value.isEmpty)return@addSnapshotListener
                if(!value.isEmpty)
                {
                    var user = arrayListOf<UserDTO>()
                    value.documents.forEach {
                        user.add(it.toObject(UserDTO::class.java)!!)
                    }
                    this@callbackFlow.sendBlocking(user)
                }
            }
        }
        awaitClose { eventListener.remove() }
    }

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


}