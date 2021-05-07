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

        photoUri.forEach {
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
            this@callbackFlow.sendBlocking(photoDownloadUrl)
        }
        awaitClose {  }
    }

    //내 구독함과 내 컨텐츠 리스트 가져오기
    @ExperimentalCoroutinesApi
    fun getSubscribeContentsWithMyContents(uid : String) = callbackFlow<ArrayList<String>> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo").whereEqualTo("uid",uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)){
                            val databaseReference2 = db.collection("User").document("UserData").collection("userInfo").document(it.id).collection("MySubscribeContentUidList").document("list")
                            val eventListener2 = databaseReference2.addSnapshotListener { value, error ->
                                if (value != null){
                                    if (value.exists()){
                                        var contentIdList = arrayListOf<String>()
                                        var postThumbnail = value.toObject(ContentDTO.PostThumbnail::class.java)

                                        postThumbnail!!.thumbnailList.forEach {
                                            contentIdList.add(it.key)
                                        }
                                        this@callbackFlow.sendBlocking(contentIdList)
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
    @ExperimentalCoroutinesApi
    fun getContents(contentId: String) = callbackFlow<Map<String,ContentDTO>> {

            val databaseReference = db.collection("Contents").document(contentId)

            val eventListener = databaseReference.get().addOnCompleteListener {
                if (it != null){
                    if (it.isSuccessful){
                        if (it.result != null){
                            var contentDTO = it.result.toObject(ContentDTO::class.java)
                            var map : MutableMap<String,ContentDTO> = HashMap()

                            map.put(it.result.id, contentDTO!!)
                            this@callbackFlow.sendBlocking(map)
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