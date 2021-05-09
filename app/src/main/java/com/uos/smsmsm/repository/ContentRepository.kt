package com.uos.smsmsm.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
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
    private val auth = FirebaseAuth.getInstance()

    //댓글 업로드
    @ExperimentalCoroutinesApi
    fun uploadComment(comment: ContentDTO.Comment, postId: String) = callbackFlow<Boolean> {
        val databaseReference = db.collection("Contents").document(postId)

        println("끼엨 $postId")
        println("댓글? $comment")

        db.runTransaction { transaction ->
            var snapshot = transaction.get(databaseReference).toObject(ContentDTO::class.java)


            //데이터 추가~
            snapshot!!.commentList.put(auth.currentUser!!.uid, comment)
            snapshot!!.commentCount = snapshot!!.commentCount!! + 1
            transaction.set(databaseReference, snapshot)
            println("추가아")
            this@callbackFlow.sendBlocking(true)
            return@runTransaction

        }
        awaitClose { }
    }

    //사진 업로드
    @ExperimentalCoroutinesApi
    fun uploadPhoto(photoUri: Uri) = callbackFlow<String> {
        var timestamp = System.currentTimeMillis()
        var imageFileName = "SMSM_" + "Content_" + timestamp + "_.png"
        var storageRef = storage.reference.child("Content_Photo").child(imageFileName)

        storageRef.putFile(photoUri)
            ?.continueWithTask { task ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener { uri ->
                this@callbackFlow.sendBlocking(uri.toString())
            }

        awaitClose { }
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
        awaitClose { }
    }

    //내 구독함과 내 컨텐츠 리스트 가져오기
    @ExperimentalCoroutinesApi
    fun getSubscribeContentsWithMyContents(uid: String) = callbackFlow<ArrayList<String>?> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid", uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            println("aaaaaaaa4")
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)) {
                            val databaseReference2 =
                                db.collection("User").document("UserData").collection("userInfo")
                                    .document(it.id).collection("MySubscribeContentUidList")
                                    .document("list")
                            val eventListener2 =
                                databaseReference2.addSnapshotListener { value, error ->
                                    if (value != null) {
                                        if (value.exists()) {
                                            var contentIdList = arrayListOf<String>()
                                            var postThumbnail =
                                                value.toObject(ContentDTO.PostThumbnail::class.java)

                                            postThumbnail!!.thumbnailList.forEach {
                                                contentIdList.add(it.key)
                                            }
                                            this@callbackFlow.sendBlocking(contentIdList)
                                        } else {
                                            this@callbackFlow.sendBlocking(null)
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
        awaitClose { }
    }

    //전체 게시글 목록에 게시글 업로드
    @ExperimentalCoroutinesApi
    fun uploadContentInContents(content: ContentDTO, uid: String) =
        callbackFlow<Map<String, ContentDTO>> {
            val databaseReference = db.collection("Contents")

            val eventListener = databaseReference.add(content).addOnCompleteListener {
                if (it != null) {
                    if (it.isSuccessful) {

                        var map: MutableMap<String, ContentDTO> = HashMap()
                        map.put(it.result.id, content)

                        this@callbackFlow.sendBlocking(map)

                    }
                }
            }

            awaitClose { eventListener }
        }

    //게시글 업로드
    @ExperimentalCoroutinesApi
    fun uploadContent(content: ContentDTO, uid: String) = callbackFlow<Map<String, ContentDTO>> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid", uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach {
                        if (it["uid"]!!.equals(uid)) {
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(it.id)
                                .collection("ContentsContainer")
                            val eventListener2 =
                                databaseReference2.add(content).addOnCompleteListener {

                                    var map: MutableMap<String, ContentDTO> = HashMap()
                                    map.put(it.result.id, content)
                                    this@callbackFlow.sendBlocking(map)
                                }.addOnFailureListener {
                                    println("게시글 업로드 실패")
                                }
                        }
                    }
                }
            }
        }
        awaitClose { }
    }

    //좋아요 이벤트
    @ExperimentalCoroutinesApi
    fun favoriteEvent(contentId: String) = callbackFlow<Boolean> {
        var tsDoc = db.collection("Contents").document(contentId)

        db.runTransaction { transaction ->
            var contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(auth.currentUser!!.uid)) {
                contentDTO.favoriteCount = contentDTO.favoriteCount!! - 1
                contentDTO.favorites.remove(auth.currentUser!!.uid)
                //내가 좋아하는 게시글 리스트에서 해당 contentId 제거
                addFavoriteContent(contentId, false)
                transaction.set(tsDoc, contentDTO)
                this@callbackFlow.sendBlocking(true)
            } else {
                contentDTO.favoriteCount = contentDTO.favoriteCount!! + 1
                contentDTO.favorites[auth.currentUser!!.uid] = true
                //내가 좋아하는 게시글 리스트에서 해당 contentId 추가
                addFavoriteContent(contentId, true)
                transaction.set(tsDoc, contentDTO)
                this@callbackFlow.sendBlocking(true)
            }

        }
        awaitClose { }
    }

    fun addFavoriteContent(contentId: String, type: Boolean) {

        println("리스트 추가하기")
        var databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid", auth.currentUser!!.uid)
        var eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it != null) {
                    if (it.result != null) {
                        if (!it.result.isEmpty) {
                            it.result.forEach {
                                if (it["uid"] == auth.currentUser!!.uid) {

                                    var addFavoriteReference =
                                        db.collection("User").document("UserData")
                                            .collection("userInfo").document(it.id)
                                            .collection("favoriteContents").document("list")
                                    println("업데이트 시작하기 ${it.id}")
                                    db.runTransaction { transaction ->
                                        var favoriteList = transaction.get(addFavoriteReference)
                                            .toObject(ContentDTO.FavoriteList::class.java)
                                        println("으어어어 $favoriteList")
                                        if (favoriteList == null) {
                                            favoriteList = ContentDTO.FavoriteList()
                                            transaction.set(addFavoriteReference, favoriteList)
                                        }
                                        if (type) {
                                            //추가
                                            println("추가")
                                            favoriteList!!.favoriteList.put(
                                                contentId,
                                                System.currentTimeMillis()
                                            )
                                            println("??????????")
                                            println("으어어어222 $favoriteList")
                                            transaction.set(addFavoriteReference, favoriteList)
                                        } else {
                                            //제거
                                            println("제거")
                                            if (favoriteList!!.favoriteList.containsKey(contentId)) {
                                                favoriteList.favoriteList.remove(contentId)
                                                println("??????????")

                                                println("으어어어2222 $favoriteList")
                                                transaction.set(addFavoriteReference, favoriteList)
                                            } else {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun isFavorite(contentId: String) = callbackFlow<Boolean> {
        println("isFavorite 실행~~")
        val databaseReference = db.collection("Contents").document(contentId)
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value != null) {
                println("value가 null이지않음")
                if (value.exists()) {
                    println("value가 존재함")
                    var content = value.toObject(ContentDTO::class.java)
                    if (content!!.favorites.containsKey(auth.currentUser!!.uid)) {
                        println("이미 좋아요를 눌러뒀습니다.")
                        this@callbackFlow.sendBlocking(true)
                    } else {
                        println("좋아요를 눌러두지않았습니다.")
                        this@callbackFlow.sendBlocking(false)
                    }
                }
            }
        }
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    fun getFavoriteCountByContentId(contentId: String) = callbackFlow<Int> {
        val databaseReference = db.collection("Contents").document(contentId)
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value != null){
                if (value.exists()){
                    var content = value.toObject(ContentDTO::class.java)
                    this@callbackFlow.sendBlocking(content!!.favoriteCount!!)
                }
            }
        }
        awaitClose {  }
    }

    @ExperimentalCoroutinesApi
    fun getContents(contentId: String) = callbackFlow<Map<String, ContentDTO>> {

        val databaseReference = db.collection("Contents").document(contentId)

        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value != null) {
                var contentDTO = value.toObject(ContentDTO::class.java)
                var map: MutableMap<String, ContentDTO> = HashMap()

                map.put(value.id, contentDTO!!)
                this@callbackFlow.sendBlocking(map)
            }
        }
        awaitClose { }
    }

    fun getUserPostContent(uid: String) = callbackFlow<Map<String, ContentDTO>> {

        println("자기 post 가져오기 실행")

        val databaseReference = db.collection("Contents")
            .whereEqualTo("uid", uid)

        val eventListener = databaseReference.addSnapshotListener { value, error ->
            if (value != null) {
                if (!value.isEmpty) {

                    var map: MutableMap<String, ContentDTO> = HashMap()
                    value.forEach {
                        map.put(it.id, it.toObject(ContentDTO::class.java)!!)
                    }
                    this@callbackFlow.sendBlocking(map)
                }
            }
        }
        awaitClose { }
    }
}