package com.uos.smsmsm.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ChatRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val rdb = FirebaseDatabase.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    //destinationUid = 채팅방의 uid

    @ExperimentalCoroutinesApi
    fun checkChatRoom(destinationUid: String) = callbackFlow<String> {
        //유저 uid가 들어간 realdb 정보가져오기
        val databaseReference = rdb.reference.child("chatrooms").orderByChild("users/" + uid).equalTo(true)
        val eventListener = databaseReference.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //채팅방이 있는경우 상대 방 uid가 있는 채팅방 의키값 반환하기
                snapshot.children.forEach{
                    var chatDTOs: ChatDTO = it.getValue(ChatDTO::class.java)!!
                    if (chatDTOs.users.containsKey(destinationUid)){
                        this@callbackFlow.sendBlocking(it.key!!)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun checkOpenChatRoom(destinationUid: String?) = callbackFlow<String> {
        //유저 uid가 들어간 realdb 정보가져오기
        if(destinationUid!=null) {
            val databaseReference = db.collection("openChatRoom").document(destinationUid)
            val eventListener = databaseReference.addSnapshotListener { value, error ->
                println(value)
                if (value != null) {
                    this@callbackFlow.sendBlocking(value.id)
                }
            }
        }
        awaitClose()

    }

    @ExperimentalCoroutinesApi
    fun createChatRoom(destinationUid: String,chatData : ChatDTO) = callbackFlow<String> {
        val databaseReference = rdb.reference.child("chatrooms")
        val eventListener = databaseReference.push()
        eventListener.setValue(chatData).addOnSuccessListener {
            this@callbackFlow.sendBlocking(eventListener.key!!)
        }
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun createOpenChatRoom(chatTitle : String?, chatData: ChatDTO) = callbackFlow<String> {
        println(" 채팅방 개설 : " + chatTitle)
        chatData.chatTitle = chatTitle
        var document = db.collection("openChatRoom").document()
        document.set(chatData).addOnSuccessListener {
            this@callbackFlow.sendBlocking(document.id)
        }
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun addChat(chatRoomUid: String, comment: ChatDTO.Comment,chatType : String) = callbackFlow<Boolean> {
        if(chatType == "personal") {
            val databaseReference = rdb.reference.child("chatrooms").child(chatRoomUid)
            val commentReference = databaseReference.child("comments")
            val timeReference = databaseReference.child("commentTimestamp")

            val commentListener = commentReference.push().setValue(comment).addOnCompleteListener {
                println("코멘트 추가 성공")
                val eventListener = timeReference.setValue(comment.timestamp).addOnCompleteListener {
                        println("날자 최신화 성공")
                        this@callbackFlow.sendBlocking(true)
                    }
            }
        }else if(chatType == "open"){
            var databaseReference = db.collection("openChatRoom").document(chatRoomUid)
            val commentReference = databaseReference.collection("comments").document()
            val commentListener = commentReference.set(comment).addOnCompleteListener {
                println("코멘트 추가 성공")
                val eventListener = databaseReference.update("commentTimestamp",comment.timestamp).addOnCompleteListener {
                        println("날자 최신화 성공")
                        this@callbackFlow.sendBlocking(true)
                    }
            }
        }
        awaitClose()
    }


    @ExperimentalCoroutinesApi
    fun getChat(chatRoomUid: String,chatType : String?) = callbackFlow<ArrayList<ChatDTO.Comment>> {

        println("메세지 가져오기 시도 : chatType : " + chatType)

        var chatData: ArrayList<ChatDTO.Comment> = arrayListOf()

        if(chatType == "personal") {
            var chatData: ArrayList<ChatDTO.Comment> = arrayListOf()
            val databaseReference = rdb.reference.child("chatrooms").child(chatRoomUid).child("comments")
            val eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            chatData.add(it.getValue(ChatDTO.Comment::class.java)!!)
                        }
                        this@callbackFlow.sendBlocking(chatData)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }else if(chatType == "open") {
            val databaseReference = db.collection("openChatRoom").document(chatRoomUid).collection("comment")
            val eventListener = databaseReference.addSnapshotListener { value, error ->
                if (value != null) {
                    value.documents.forEach {
                        chatData.add(it.toObject(ChatDTO.Comment::class.java)!!)
                    }
                    this@callbackFlow.sendBlocking(chatData)
                }
            }
        }
        awaitClose { }
    }

    //채팅룸 리스트 가져오기
    @ExperimentalCoroutinesApi
    fun getChatRoomList(uid: String) = callbackFlow<ArrayList<ChatDTO>> {

        //1:1 채팅
        var personalChat : ArrayList<ChatDTO> = arrayListOf()
        var openChat : ArrayList<ChatDTO> = arrayListOf()
        var resultChat : ArrayList<ChatDTO> = arrayListOf()

        //1:1 채팅 레퍼런스
        val personalReference = rdb.reference.child("chatrooms").orderByChild("users/$uid").equalTo(true)
        //오픈 채팅 레퍼런스
        val openReference = db.collection("openChatRoom")
        val personalListener = personalReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                println("1:1 채팅 목록 가져오기")
                //채팅룸 리스트 초기화
                personalChat.clear()                //리스트에 리얼타임 db의 채팀룸 리스트 추가
                for (item in snapshot.children) {
                    personalChat.add(item.getValue(ChatDTO::class.java)!!)
                }
                val openListener = openReference.addSnapshotListener { value, error ->
                    println("오픈 채팅 목록 가져오기")
                    openChat.clear()
                    resultChat.clear()
                    if (value!=null) {
                        value.documents.forEach {
                            openChat.add(it.toObject(ChatDTO::class.java)!!)
                        }
                    }
                    //두개의 리스트를 추가하여 정렬하여 반환
                    resultChat.addAll(openChat)
                    resultChat.addAll(personalChat)
                    resultChat = ArrayList(
                        resultChat.sortedWith(compareBy<ChatDTO>({ it.commentTimestamp })).reversed()
                    )
                    this@callbackFlow.sendBlocking(resultChat)
                }
            }
        })
        awaitClose {  }
    }
}