package com.uos.smsmsm.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val rdb = FirebaseDatabase.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    @ExperimentalCoroutinesApi
    fun checkChatRoom(destinationUid: String) = callbackFlow<String> {
        val databaseReference = rdb.reference.child("chatrooms").orderByChild("users/" + uid).equalTo(true)
        val eventListener = databaseReference.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    var chatDTOs: ChatDTO = it.getValue(ChatDTO::class.java)!!

                    if (chatDTOs.users.containsKey(destinationUid)){
                        this@callbackFlow.sendBlocking(it.key!!)
                    }
                }

                println("checkChatRoom 실행완료")
            }

            override fun onCancelled(error: DatabaseError) {
                println("checkChatRoom 데이터 조회 실패")
            }

        })

        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun createChatRoom(destinationUid: String, chatData: ChatDTO) = callbackFlow<Boolean> {
        val databaseReference = rdb.reference.child("chatrooms")
        val eventListener = databaseReference.push().setValue(chatData).addOnSuccessListener {
            println("Createchatroom 실행 완료오오오오오옼")
            checkChatRoom(destinationUid)
            this@callbackFlow.sendBlocking(true)
        }
        awaitClose()
    }
    @ExperimentalCoroutinesApi
    fun addChat(chatRoomUid: String, comment: ChatDTO.Comment) = callbackFlow<Boolean> {
        val databaseReference = rdb.reference.child("chatrooms").child(chatRoomUid).child("comments")
        val eventListener = databaseReference.push().setValue(comment).addOnCompleteListener {
            this@callbackFlow.sendBlocking(true)
        }
        awaitClose()
    }

    @ExperimentalCoroutinesApi
    fun getChat(chatRoomUid: String) = callbackFlow<ArrayList<ChatDTO.Comment>> {
        var chatData : ArrayList<ChatDTO.Comment> = arrayListOf()
        val databaseReference = rdb.reference.child("chatrooms").child(chatRoomUid).child("comments")
        val eventListener = databaseReference.addValueEventListener(object : ValueEventListener{
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
        
        awaitClose {  }
    }
}