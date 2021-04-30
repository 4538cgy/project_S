package com.uos.smsmsm.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class ContentRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    //사진 업로드
    @ExperimentalCoroutinesApi
    fun uploadPhoto(photoUri : Uri) = callbackFlow<String> {
        var timestamp = System.currentTimeMillis()
        var imageFileName = "SMSM_" + "Content_" + timestamp + "_.png"
        var storageRef = storage.reference.child("Content_Photo").child(imageFileName)

        storageRef.putFile(photoUri)
            ?.continueWithTask { task ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener { uri ->
                this@callbackFlow.sendBlocking(uri.toString())
            }

        awaitClose {  }
    }

    //사진 여러장 업로드
    @ExperimentalCoroutinesApi
    fun uploadPhotoList(photoUri: ArrayList<Uri>) = callbackFlow<ArrayList<String>> {
        var photoDownloadUrl = arrayListOf<String>()
        var timestamp = System.currentTimeMillis()
        var imageFileName = "SMSM_" + "Content_" + timestamp + "_.png"
        var storageRef = storage.reference.child("Content_Photo").child(imageFileName)

        println("uloadPhotoList 진행")
        println("photoUri의 크기 ${photoUri.size}")

        photoUri.forEach {
            println("현재 업로드하는 photo의 uri = $it")
            storageRef.putFile(it)
                ?.continueWithTask { task ->
                    return@continueWithTask storageRef.downloadUrl
                }?.addOnSuccessListener { uri ->
                    println("사진 업로드 성공")
                    photoDownloadUrl.add(uri.toString())
                }.addOnFailureListener {
                    println(" 사진 업로드 실패 cause = ${it.toString()}")
                }.addOnCanceledListener {
                    println(" 사진 업로드 취소 cause = ${it.toString()}")
                }
            println("스코프 내부 ${photoDownloadUrl.toString()}")
            this@callbackFlow.sendBlocking(photoDownloadUrl)
        }
        println("스코프 외부 ${photoDownloadUrl.toString()}")

        awaitClose {  }
    }

    //게시글 업로드
    @ExperimentalCoroutinesApi
    fun uploadContent(content : ContentDTO, uid : String) = callbackFlow<Boolean> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo").whereEqualTo("uid" , uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful){
                if (it.result!=null){
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)){
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(it.id)
                                .collection("ContentsContainer")
                                .document()

                            val eventListener2 = databaseReference2.set(content).addOnCompleteListener {
                                println("게시글 업로드 완료")
                                this@callbackFlow.sendBlocking(true)
                            }.addOnFailureListener {
                                println("게시글 업로드 실패")
                                this@callbackFlow.sendBlocking(false)
                            }
                        }
                    }
                }
            }
        }
        awaitClose {  }
    }

    fun getUserPostContent(uid : String) = callbackFlow<ArrayList<ContentDTO>> {
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
                                .collection("ContentsContainer")

                            val eventListener2 = databaseReference2.addSnapshotListener { value, error ->
                                if(value != null){
                                    if (!value.isEmpty){
                                        var contents = value.toObjects(ContentDTO::class.java)
                                        var contentsList = arrayListOf<ContentDTO>()
                                        contentsList.addAll(contents)
                                        this@callbackFlow.sendBlocking(contentsList)
                                    }
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