package com.uos.smsmsm.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ContentDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class BackgroundRepository {

    private val db = FirebaseFirestore.getInstance()

    @ExperimentalCoroutinesApi
    fun copyUserContents(uid : String) = callbackFlow<Map<String,ContentDTO.PostThumbnail>> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
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

                                pasteUserContentsMyContainer(uid,documentSnapshot.id,map)
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
    fun pasteUserContentsMyContainer(uid: String,documentId : String,contentList : Map<String,ContentDTO.PostThumbnail>) = callbackFlow<Boolean> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)){

                            contentList.forEach {
                                val databaseReference2 = db.collection("User").document("UserData")
                                    .collection("userInfo")
                                    .document(documentId)
                                    .collection("MySubscribeContentsUidList")
                                    .document(it.key)

                                val eventListener2 = databaseReference2.set(it.value).addOnCompleteListener {
                                    this@callbackFlow.sendBlocking(true)
                                }.addOnFailureListener {
                                    this@callbackFlow.sendBlocking(false)
                                }.addOnCanceledListener {
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