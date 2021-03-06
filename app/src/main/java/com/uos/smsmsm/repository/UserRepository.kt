package com.uos.smsmsm.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.SubscribeDTO
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
        println("친구목록 가져오기 실행")
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
                                .collection("Subscribe")
                                .document("subscribe")

                            val eventListener2 = databaseReference2.get().addOnCompleteListener {
                                if (it.isSuccessful){
                                    if (it.result.exists()){
                                        println("친구 목록이 있습니다. ${it.result.toString()}")
                                        var data = it.result.toObject(SubscribeDTO::class.java)
                                        data!!.subscribingList.forEach {
                                            if (it.key.equals(destinationUid)){
                                                this@callbackFlow.sendBlocking(true)
                                                return@forEach
                                            }else{
                                                this@callbackFlow.sendBlocking(false)
                                            }
                                        }
                                    }else{
                                        println("친구 목록이 없습니다.")
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
    fun addFriend(uid: String, destinationUid: String ) = callbackFlow<String> {

        val databaseReference = db.collection("User")
            .document("UserData")
            .collection("userInfo")
            .whereEqualTo("uid", uid)
        val eventListener = databaseReference.get().addOnCompleteListener { dataSnapshot ->
            if (dataSnapshot.isSuccessful) {
                if (dataSnapshot.result != null) {
                    dataSnapshot.result.documents.forEach { foreachDocumentSnapshot ->
                        println("으아아아아 ${foreachDocumentSnapshot.data.toString()}")
                        println("으어어어어 ${foreachDocumentSnapshot["uid"]}")
                        if (foreachDocumentSnapshot["uid"]!!.equals(uid)) {
                            val tsDocSubscribing =
                                db.collection("User").document("UserData").collection("userInfo")
                                    .document(foreachDocumentSnapshot.id).collection("Subscribe")
                                    .document("subscribe")

                            db.runTransaction { transaction ->

                                var friendsDTO = transaction.get(tsDocSubscribing)
                                    .toObject(SubscribeDTO::class.java)

                                //데이터가 없으면 데이터 생성
                                if (friendsDTO == null) {
                                    println("복사할 컬렉션 생성")
                                    friendsDTO = SubscribeDTO()
                                    var subscribingDTO = SubscribeDTO.SubscribingDTO()
                                    subscribingDTO.uid = destinationUid
                                    subscribingDTO.timestamp = System.currentTimeMillis()
                                    friendsDTO.subscribingCount = 1
                                    friendsDTO.subscribingList.put(destinationUid, subscribingDTO)

                                    transaction.set(tsDocSubscribing, friendsDTO)
                                    this@callbackFlow.sendBlocking("SUBSCRIBE_CREATE")
                                    return@runTransaction
                                }

                                //구독하기를 발생시킨 사람의 db 접근
                                if (friendsDTO.subscribingList.containsKey(uid)) {
                                    friendsDTO.subscribingCount = friendsDTO.subscribingCount!! - 1
                                    friendsDTO.subscribingList.remove(destinationUid)

                                    transaction.set(tsDocSubscribing, friendsDTO)
                                    this@callbackFlow.sendBlocking("SUBSCRIBE_UPDATE" )
                                    return@runTransaction
                                } else {
                                    var subscribingDTO = SubscribeDTO.SubscribingDTO()
                                    subscribingDTO.uid = destinationUid
                                    subscribingDTO.timestamp = System.currentTimeMillis()
                                    friendsDTO.subscribingCount = friendsDTO.subscribingCount!! + 1
                                    friendsDTO.subscribingList.put(destinationUid, subscribingDTO)

                                    transaction.set(tsDocSubscribing, friendsDTO)
                                    this@callbackFlow.sendBlocking("SUBSCRIBE_DELETE")
                                    return@runTransaction
                                }
                                println("내 db에 추가 끝")

                            }

                            //구독을 당한자의 db 접근
                            
                            println("상대 db에 추가")
                            val databaseReference2 = db.collection("User")
                                .document("UserData")
                                .collection("userInfo")
                                .whereEqualTo("uid", destinationUid)

                            val eventListener2 =
                                databaseReference2.get().addOnCompleteListener { dataSnapshot2 ->
                                    if (dataSnapshot2.isSuccessful) {
                                        if (dataSnapshot2.result != null) {
                                            dataSnapshot2.result.documents.forEach { foreachDocumentSnapshot2 ->
                                                if (foreachDocumentSnapshot2["uid"]!!.equals(
                                                        destinationUid
                                                    )
                                                ) {
                                                    val tsDocSubscriber =
                                                        db.collection("User").document("UserData")
                                                            .collection("userInfo")
                                                            .document(foreachDocumentSnapshot2.id)
                                                            .collection("Subscribe")
                                                            .document("subscribe")

                                                    println("으아아아 ${foreachDocumentSnapshot2.id}")
                                                    println("수정 시작")
                                                    db.runTransaction { transaction2 ->
                                                        println("수정 진행중")
                                                        var friendsDTO2 =
                                                            transaction2.get(tsDocSubscriber)
                                                                .toObject(SubscribeDTO::class.java)

                                                        //데이터가 없으면 데이터 생성

                                                        if (friendsDTO2 == null) {
                                                            println("데이터가 없음 실행")
                                                            friendsDTO2 = SubscribeDTO()
                                                            var subscriberDTO =
                                                                SubscribeDTO.SubScriberDTO()
                                                            subscriberDTO.uid = uid
                                                            subscriberDTO.timestamp =
                                                                System.currentTimeMillis()
                                                            friendsDTO2.subscriberCount = 1
                                                            friendsDTO2.subscriberList.put(
                                                                uid,
                                                                subscriberDTO

                                                            )

                                                            transaction2.set(
                                                                tsDocSubscriber,
                                                                friendsDTO2
                                                            )

                                                            this@callbackFlow.sendBlocking("SUBSCRIBER_CREATE")
                                                            return@runTransaction
                                                        }

                                                        //구독하기를 발생시킨 사람의 db 접근
                                                        if (friendsDTO2.subscriberList.containsKey(
                                                                uid
                                                            )
                                                        ) {
                                                            println("데이터가 있음 true")
                                                            friendsDTO2.subscriberCount =
                                                                friendsDTO2.subscriberCount!! - 1
                                                            friendsDTO2.subscriberList.remove(uid)
                                                            transaction2.set(
                                                                tsDocSubscriber,
                                                                friendsDTO2
                                                            )
                                                            this@callbackFlow.sendBlocking("SUBSCRIBER_UPDATE")
                                                            return@runTransaction
                                                        } else {
                                                            println("데이터가 있음 false")
                                                            var subscriberDTO =
                                                                SubscribeDTO.SubScriberDTO()
                                                            subscriberDTO.uid = uid
                                                            subscriberDTO.timestamp =
                                                                System.currentTimeMillis()
                                                            friendsDTO2.subscriberCount =
                                                                friendsDTO2.subscriberCount!! + 1
                                                            friendsDTO2.subscriberList.put(
                                                                uid,
                                                                subscriberDTO
                                                            )
                                                            transaction2.set(
                                                                tsDocSubscriber,
                                                                friendsDTO2
                                                            )
                                                            this@callbackFlow.sendBlocking("SUBSCRIBER_DELETE")
                                                            return@runTransaction
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
        }
        awaitClose {  }
    }

    //친구 목록 가져오기
    fun getFriendsList(uid : String) = callbackFlow<ArrayList<String>> {
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
                                .collection("Subscribe")
                                .document("subscribe")


                            val eventListener2 = databaseReference2.addSnapshotListener { value, error ->
                                if (value != null){

                                    if (value.exists()) {


                                        var subscribeDTO = value.toObject(SubscribeDTO::class.java)
                                        var arrayList = arrayListOf<String>()


                                        subscribeDTO!!.subscribingList.keys.forEach {
                                            arrayList.add(it)
                                        }


                                        this@callbackFlow.sendBlocking(arrayList)
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