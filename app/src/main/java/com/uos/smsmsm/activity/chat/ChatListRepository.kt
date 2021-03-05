package com.uos.smsmsm.activity.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.uos.smsmsm.data.ChatDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

    private val ChatRoomDataBase = FirebaseDatabase.getInstance()


    init {

    }

    //listen data everytime
    suspend fun DatabaseReference.getSnapshotValue(): DataSnapshot{
        return withContext(Dispatchers.IO){
            suspendCoroutine<DataSnapshot> { continuation ->
                addListenerForSingleValueEvent(FirebaseValueEventListener(
                    onDataChange = { continuation.resume(it)},
                    onError = { continuation.resumeWithException(it.toException())}
                ))
            }
        }
    }




    //get data once
    fun getChatRoomListData() = flow<State<ArrayList<ChatDTO>>> {
        //Loading state
        emit(State.loading())

        var chatRoomData = arrayListOf<ChatDTO>()

        val snapshot = ChatRoomDataBase.reference
            .child("test")
            .child("chatrooms")
            .orderByChild("users/$uid")
            .equalTo(true)
            .get().await()

        snapshot.children.forEach {
            chatRoomData.add( it.getValue(ChatDTO::class.java)!!)
        }


        emit(State.success(chatRoomData))
    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getChatListData(){

    }

}

//ValueEventListener - RDB
class FirebaseValueEventListener(val onDataChange: (DataSnapshot) -> Unit, val onError: (DatabaseError) -> Unit) : ValueEventListener {
    override fun onDataChange(data: DataSnapshot) = onDataChange.invoke(data)
    override fun onCancelled(error: DatabaseError) = onError.invoke(error)
}

sealed class State<T>{
    class Loading<T> : State<T>()
    data class Success<T>(val data: T) : State<T>()
    data class Failed<T>(val message: String) : State<T>()
    data class Complete<T>(val data: T) : State<T>()
    data class Cancel<T>(val message: String) : State<T>()
    data class SuccessListening<T>(val data: T) : State<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data : T) = Success(data)
        fun <T> failed(message: String) = Failed<T>(message)
        fun <T> complete(data : T) = Complete(data)
        fun <T> cancel(message : String) = Cancel<T>(message)
        fun <T> successListening(data : T) = SuccessListening(data)
    }
}