package com.uos.smsmsm.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    /*
    *
    *


    How to get SnapShot in ViewModel
    * 0. declare repository
    * 1. access 'viewModelScope'
    * 2. launch Dispatchers.IO
    * 3. access repository
    * 4. call repository method
    * 5. collect data

* ex )
        viewModelScope.launch(Dispatchers.IO) {
            repository.Observe().collect {
                println("data print in viewmodel = " + it.toString())
            }
        }


        *
        *
        */

    var uid = FirebaseAuth.getInstance()

    //Firebase Firestore get data from snapshot listener
    fun TestFirestoreSnapshotObserve() = callbackFlow<UserDTO> {

        val databaseReference = FirebaseFirestore.getInstance().collection("test").document("user")
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            var userData = value?.toObject(UserDTO::class.java)
            this@callbackFlow.sendBlocking(userData!!)



        }

        awaitClose {
            eventListener.remove()
        }
    }


    //Firebase RTDB get data from snapshot listener
    fun TestFirebaseRDBObserve()= callbackFlow<ChatDTO> {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("test").child("chatRoomList")
        val eventListener = databaseReference.addValueEventListener(FirebaseValueEventListener(
            onDataChange = {it.let {
                var chatDATA = it.getValue(ChatDTO::class.java)
                this@callbackFlow.sendBlocking(chatDATA!!)
            }},
            onError = {this@callbackFlow.close(it.toException())}
        ))
        awaitClose {
            databaseReference.removeEventListener(eventListener)
        }
    }

    //get data once
    fun TestWithStateObserver() = flow<State<ArrayList<ChatDTO>>> {
        //Loading state
        emit(State.loading())
        var chatRoomData = arrayListOf<ChatDTO>()
        val snapshot = FirebaseDatabase.getInstance().reference
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

}

//data load state
sealed class State<T> {
    class Loading<T> : State<T>()
    data class Success<T>(val data: T) : State<T>()
    data class Failed<T>(val message: String) : State<T>()
    data class Complete<T>(val data: T) : State<T>()
    data class Cancel<T>(val message: String) : State<T>()
    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failed(message: String) = Failed<T>(message)
        fun <T> complete(data: T) = Complete(data)
        fun <T> cancel(message: String) = Cancel<T>(message)
    }
}


//ValueEventListener - RDB
class FirebaseValueEventListener(val onDataChange: (DataSnapshot) -> Unit, val onError: (DatabaseError) -> Unit) :
    ValueEventListener {
    override fun onDataChange(data: DataSnapshot) = onDataChange.invoke(data)
    override fun onCancelled(error: DatabaseError) = onError.invoke(error)
}