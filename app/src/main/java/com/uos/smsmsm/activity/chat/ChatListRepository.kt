package com.uos.smsmsm.activity.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uos.smsmsm.data.ChatDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatListRepository {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    // Apply Camelcase
    private val chatRoomDataBase = FirebaseDatabase.getInstance()

    init {
    }

    // listen data everytime
    suspend fun DatabaseReference.getSnapshotValue(): DataSnapshot {
        return withContext(Dispatchers.IO) {
            suspendCoroutine<DataSnapshot> { continuation ->
                addListenerForSingleValueEvent(
                    FirebaseValueEventListener(
                        onDataChange = { continuation.resume(this) },
                        onError = { continuation.resumeWithException(this.toException()) }
                    )
                )
            }
        }
    }

    fun getChatListData() {
    }

    @ExperimentalCoroutinesApi // Add
    // get data once
    fun getChatRoomListData() = flow { // Remove unnecessary
        // Loading state
        emit(State.loading())

        // Change ArrayList -> MutableList
        val chatRoomData = mutableListOf<ChatDTO>()

        val snapshot = chatRoomDataBase.reference
            .child("test")
            .child("chatrooms")
            .orderByChild("users/$uid")
            .equalTo(true)
            .get().await()

        snapshot.children.forEach {
            chatRoomData.add(it.getValue(ChatDTO::class.java)!!)
        }

        emit(State.success(chatRoomData))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}

// ValueEventListener - RDB
class FirebaseValueEventListener( // Change scope
    val onDataChange: DataSnapshot.() -> Unit,
    val onError: DatabaseError.() -> Unit
) : ValueEventListener {
    override fun onDataChange(data: DataSnapshot) = onDataChange.invoke(data)
    override fun onCancelled(error: DatabaseError) = onError.invoke(error)
}

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Success<T>(val data: T) : State<T>()
    data class Failed<T>(val message: String) : State<T>()
    data class Complete<T>(val data: T) : State<T>()
    data class Cancel<T>(val message: String) : State<T>()
    data class SuccessListening<T>(val data: T) : State<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failed(message: String) = Failed<T>(message)
        fun <T> complete(data: T) = Complete(data)
        fun <T> cancel(message: String) = Cancel<T>(message)
        fun <T> successListening(data: T) = SuccessListening(data)
    }
}
