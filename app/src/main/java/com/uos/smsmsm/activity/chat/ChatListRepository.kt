package com.uos.smsmsm.activity.chat

import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.UserDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatListRepository {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val ChatRoomDataBase = FirebaseDatabase.getInstance()

    private val scope = CoroutineScope(Dispatchers.IO)



    sealed class Data<UserDTO> {
        class getUserData<UserDTO> : Data<UserDTO>()
        companion object{
            fun <UserDTO> getUserDataBASE(data : UserDTO) = getUserData<UserDTO>()
        }
    }



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
    @ExperimentalCoroutinesApi
    private val flow2 = callbackFlow<UserDTO> {

        val channel = Channel<UserDTO>()
        val databaseReference = FirebaseFirestore.getInstance().collection("test").document("user")
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            var userData = value?.toObject(UserDTO::class.java)
            this@callbackFlow.sendBlocking(userData!!)
            offer(userData!!)


        }

        awaitClose {
            eventListener.remove()
        }
    }
    fun userObserver(){
        println("유저 데이터 불러오기실해애애애애애앵")

        scope.launch {
            flow2.collect { userData ->

            }
        }
    }



    //이걸로 수신대기해야합니다...
    @ExperimentalCoroutinesApi
    private val flow = callbackFlow<ChatDTO> {

        val databaseReference = ChatRoomDataBase.reference.child("test").child("chatRoomList")
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

    fun Observe() = callbackFlow<UserDTO> {
        val channel = Channel<UserDTO>()
        val databaseReference = FirebaseFirestore.getInstance().collection("test").document("user")
        val eventListener = databaseReference.addSnapshotListener { value, error ->
            var userData = value?.toObject(UserDTO::class.java)
            this@callbackFlow.sendBlocking(userData!!)



        }

        awaitClose {
            eventListener.remove()
        }
    }



    //수신대기 켜는것..
    fun chatObserver(){


        scope.launch {
            flow.collect { chatData ->
                //process
                var comment =  chatData.comments

            }
        }
    }

    fun testObserber(){

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


