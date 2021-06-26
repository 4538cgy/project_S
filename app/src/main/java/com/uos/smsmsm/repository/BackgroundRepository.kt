package com.uos.smsmsm.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.SubscribeDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class BackgroundRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    //내가 구독한 유저의 contents 모두 긁어오기
    @ExperimentalCoroutinesApi
    fun copyUserContents(uid : String) = callbackFlow<Map<String,ContentDTO>> {
        val databaseReference = db.collection("Contents").whereEqualTo("uid",uid)

        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {

                    var contentDTOs = it.result.toObjects(ContentDTO::class.java)
                    var map : MutableMap<String,ContentDTO> = HashMap()

                    it.result.forEach { result ->
                        map.put(result.id, result.toObject(ContentDTO::class.java))
                    }
                    this@callbackFlow.sendBlocking(map)
                }
            }
        }
        awaitClose {  }
    }
    //글 작성할때 나를 구독하고 있는 유저들에게 postId 보내기
    @ExperimentalCoroutinesApi
    fun addContentInSubscribeUserContainer(postThumbnail: ContentDTO.PostThumbnail, subscribeUidList: ArrayList<String>) = callbackFlow<Boolean> {
        subscribeUidList.add(auth.currentUser!!.uid)
        subscribeUidList.forEach {subscribeUid ->
            val databaseReference = db.collection("User").document("UserData").collection("userInfo")
                .whereEqualTo("uid",subscribeUid)
            val eventListener = databaseReference.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result != null) {
                        it.result.documents.forEach { document ->
                            if (document["uid"]!!.equals(subscribeUid)) {
                                val tsDocSubscribing =
                                    db.collection("User").document("UserData").collection("userInfo")
                                        .document(document.id).collection("MySubscribeContentUidList")
                                        .document("list")
                                db.runTransaction {transaction ->
                                    var thumbnail = transaction.get(tsDocSubscribing).toObject(ContentDTO.PostThumbnail::class.java)
                                    if (thumbnail == null){
                                        thumbnail = postThumbnail
                                        transaction.set(tsDocSubscribing,thumbnail)
                                        this@callbackFlow.sendBlocking(true)
                                        return@runTransaction
                                    }else {
                                        var data = ContentDTO.PostThumbnail.Thumbnail()
                                        postThumbnail.thumbnailList.forEach {
                                            data.uid = it.value.uid
                                            data.timestamp = it.value.timestamp
                                            thumbnail.thumbnailList.put(it.key, data)
                                        }
                                        transaction.set(tsDocSubscribing, thumbnail)
                                        this@callbackFlow.sendBlocking(true)
                                        return@runTransaction
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


    @ExperimentalCoroutinesApi
    fun getSubscribeUserList(uid : String) = callbackFlow<ArrayList<String>?> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",auth.currentUser!!.uid)
        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach { document ->
                        if (document["uid"]!!.equals(auth.currentUser!!.uid)) {
                            val databaseReference2 = db.collection("User").document("UserData")
                                .collection("userInfo")
                                .document(document.id)
                                .collection("Subscribe")
                                .document("subscribe")
                            val eventListener = databaseReference2.get().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    if (it != null) {
                                        if (it.result.exists()) {

                                            var subscribeDTO = SubscribeDTO()
                                            subscribeDTO =
                                                it.result.toObject(SubscribeDTO::class.java)!!
                                            var subscribeUidList = arrayListOf<String>()
                                            subscribeDTO.subscriberList.forEach {
                                                subscribeUidList.add(it.key)
                                            }
                                            this@callbackFlow.sendBlocking(subscribeUidList)
                                        }else{
                                            this@callbackFlow.sendBlocking(null)
                                        }
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

    @ExperimentalCoroutinesApi
    fun pasteUserContentsMyContainer(contentList : Map<String,ContentDTO>) = callbackFlow<Boolean> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",auth.currentUser!!.uid)
        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach { document ->
                        if (document["uid"]!!.equals(auth.currentUser!!.uid)) {
                            val tsDocSubscribing =
                                db.collection("User").document("UserData").collection("userInfo")
                                    .document(document.id).collection("MySubscribeContentUidList")
                                    .document("list")
                            db.runTransaction {transaction ->
                                var thumbnail = transaction.get(tsDocSubscribing).toObject(ContentDTO.PostThumbnail::class.java)
                                if (thumbnail == null){
                                    thumbnail = ContentDTO.PostThumbnail()
                                    contentList.forEach {
                                        var data = ContentDTO.PostThumbnail.Thumbnail()
                                        data.timestamp = it.value.timestamp
                                        data.uid = it.value.uid
                                        thumbnail!!.thumbnailList.put(it.key,data)
                                    }
                                    transaction.set(tsDocSubscribing,thumbnail)
                                    this@callbackFlow.sendBlocking(true)
                                    return@runTransaction
                                }else {
                                    thumbnail = ContentDTO.PostThumbnail()
                                    contentList.forEach {
                                        var data = ContentDTO.PostThumbnail.Thumbnail()
                                        data.timestamp = it.value.timestamp
                                        data.uid = it.value.uid
                                        thumbnail.thumbnailList.put(it.key,data)
                                    }
                                    transaction.set(tsDocSubscribing, thumbnail)
                                    this@callbackFlow.sendBlocking(true)
                                    return@runTransaction
                                }
                            }
                        }
                    }
                }
            }
        }
        awaitClose {  }
    }

    @ExperimentalCoroutinesApi
    fun deleteUserContentsMyContainer(uid : String) = callbackFlow<Boolean> {
        val databaseReference = db.collection("User").document("UserData").collection("userInfo")
            .whereEqualTo("uid",auth.currentUser!!.uid)
        val eventListener = databaseReference.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result != null) {
                    it.result.documents.forEach { document ->
                        if (document["uid"]!! == (auth.currentUser!!.uid)) {
                            val tsDocSubscribing =
                                db.collection("User").document("UserData").collection("userInfo")
                                    .document(document.id).collection("MySubscribeContentUidList")
                                    .document("list")
                            db.runTransaction { transaction ->
                                var thumbnail = transaction.get(tsDocSubscribing)
                                    .toObject(ContentDTO.PostThumbnail::class.java)
                                if (thumbnail == null) {
                                    this@callbackFlow.sendBlocking(true)
                                    return@runTransaction
                                } else {
                                    val dummy = ContentDTO.PostThumbnail()
                                    for (i in thumbnail.thumbnailList) {
                                        if (i.value.uid != uid) {
                                            dummy.thumbnailList[i.key] = i.value
                                        }
                                    }
                                    transaction.set(tsDocSubscribing, dummy)
                                    this@callbackFlow.sendBlocking(true)
                                    return@runTransaction
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