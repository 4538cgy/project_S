package com.uos.smsmsm.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ContentDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class BackgroundRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @ExperimentalCoroutinesApi
    fun copyUserContents(uid : String) = callbackFlow<Map<String,ContentDTO.PostThumbnail>> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    println("복사완료 ${it.result.documents.toString()}")
                    it.result.documents.forEach {documentSnapshot ->
                        if (documentSnapshot["uid"]!!.equals(uid)){
                            
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(documentSnapshot.id)
                                .collection("ContentsContainer")


                            val eventListener2 = databaseReference2.addSnapshotListener { value, error ->
                                var map : MutableMap<String,ContentDTO.PostThumbnail> = HashMap()
                                value!!.forEach {
                                    var thumbnail = ContentDTO.PostThumbnail(it["uid"].toString(),it["timestamp"].toString())
                                    map.put(it.id.toString(),thumbnail)
                                }





                                this@callbackFlow.sendBlocking(map)
                            }
                        }

                    }
                }
            }
        }
        awaitClose {  }
    }

    @ExperimentalCoroutinesApi
    fun addContentInSubscribeUserContainer(uid : String) = callbackFlow<Boolean> {
        getSubscribeUserList(uid)
    }

    fun getSubscribeUserList(uid : String){

    }

    @ExperimentalCoroutinesApi
    fun pasteUserContentsMyContainer(contentList : Map<String,ContentDTO.PostThumbnail>) = callbackFlow<Boolean> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",auth.currentUser!!.uid)
        println("으어아아아")
        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                println("붙여 넣기 됨?")
                if (it.result != null) {
                    it.result.documents.forEach { document ->
                        if (document["uid"]!!.equals(auth.currentUser!!.uid)){
                            println("붙여넣기 시작")
                            contentList.forEach {
                                val databaseReference2 = db.collection("User").document("UserData")
                                    .collection("userInfo")
                                    .document(document.id)
                                    .collection("MySubscribeContentsUidList")
                                    .document(it.key)

                                val eventListener2 = databaseReference2.set(it.value).addOnCompleteListener {
                                    println(" 성공성공서공")
                                    this@callbackFlow.sendBlocking(true)
                                }.addOnFailureListener {
                                    println("실패임")
                                    this@callbackFlow.sendBlocking(false)
                                }.addOnCanceledListener {
                                    println("실패임")
                                    this@callbackFlow.sendBlocking(false)
                                }
                            }

                        }

                    }
                }
            }
        }
        awaitClose {  }
    }
}