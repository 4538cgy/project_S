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

    //전체 게시글 목록에 게시글 업로드
    @ExperimentalCoroutinesApi
    fun uploadContentInContents(content: ContentDTO, uid: String) = callbackFlow<Map<String,ContentDTO>> {
        val databaseReference = db.collection("Contents")

        val eventListener = databaseReference.add(content).addOnCompleteListener {
            if (it != null){
                if(it.isSuccessful){

                    var map : MutableMap<String,ContentDTO> = HashMap()
                    map.put(it.result.id,content)

                    this@callbackFlow.sendBlocking(map)

                }
            }
        }

        awaitClose { eventListener }
    }

    //게시글 업로드
    @ExperimentalCoroutinesApi
    fun uploadContent(content : ContentDTO, uid : String) = callbackFlow<Map<String,ContentDTO>> {
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


                            val eventListener2 = databaseReference2.add(content).addOnCompleteListener {

                                println("게시글 업로드 완료")
                                var map : MutableMap<String,ContentDTO> = HashMap()
                                map.put(it.result.id,content)
                                this@callbackFlow.sendBlocking(map)
                            }.addOnFailureListener {
                                println("게시글 업로드 실패")
                            }
                        }
                    }
                }
            }
        }
        awaitClose {  }
    }

    fun getUserPostContent(uid : String) = callbackFlow<Map<String,ContentDTO>> {
        val databaseReference = db.collection("User")
            .document("UserData")
            .collection("userInfo")
            .whereEqualTo("uid", uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)){
                            val databaseReference2 = db.collection("Contents").whereEqualTo("uid",uid)

                            val eventListener2 = databaseReference2.addSnapshotListener { value, error ->
                                if(value != null){
                                    if (!value.isEmpty){

                                        var map : MutableMap<String,ContentDTO> = HashMap()

                                        value.forEach {
                                            map.put(it.id,it.toObject(ContentDTO::class.java))
                                        }

                                        this@callbackFlow.sendBlocking(map)
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