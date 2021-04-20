package com.uos.smsmsm.viewmodel

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.SearchView
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.data.ChatDTO
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.repository.ChatRepository
import com.uos.smsmsm.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// 채팅 / Timeline / 친구 찾기등 소셜 네트워크 기능 viewmodel
class SNSUtilViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(){

    var recyclerData : MutableLiveData<ArrayList<RecyclerDefaultModel>> = MutableLiveData()
    var edittextText : MutableLiveData<String> = MutableLiveData()
    var chatRoomUid : MutableLiveData<String> = MutableLiveData()
    var chatList : MutableLiveData<ArrayList<ChatDTO.Comment>> = MutableLiveData()
    var searchUserResult : MutableLiveData<ArrayList<UserDTO>> = MutableLiveData()
    var searchContentResult : MutableLiveData<ArrayList<RecyclerDefaultModel>> = MutableLiveData()
    var chatRoomList : MutableLiveData<ArrayList<ChatDTO>> = MutableLiveData()

    //테스트 목적
    var testUserList : MutableLiveData<ArrayList<UserDTO>> = MutableLiveData()
    //본목적
    var userList : MutableLiveData<ArrayList<UserDTO>> = MutableLiveData()

    //친구 목록 리스트의 상태
    var friendsListState : MutableLiveData<String> = MutableLiveData()

    val userRepository = UserRepository()
    val chatRepository = ChatRepository()

    val auth = FirebaseAuth.getInstance()

    //유저 검색 서치 뷰 리스너
    fun searchUserQueryTextListener() = object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            //검색 버튼 클릭시
            var emptyList = emptyList<String>()
            getSearchUserList(query!!)
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            //검색어 변경 시
            filteringUserList(newText!!)
            return true
        }

    }

    fun filteringUserList(newText: String){
        //newText 기반으로 유저 데이터 필터링
        var pathList = arrayListOf<UserDTO>()
        userList.value?.forEach {
            if (it.toString().contains(newText))
            {
                pathList.add(it)
            }
        }
        searchUserResult.postValue(pathList)
        searchUserResultDataConvertRecyclerData()
    }

    fun searchUserResultDataConvertRecyclerData(){

        var list = arrayListOf<RecyclerDefaultModel>()

        searchUserResult.value?.forEachIndexed{ index ,it ->
            list.add(RecyclerDefaultModel(RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,"",searchUserResult.value!![index].uid,null,
                searchUserResult.value!![index].userName!!,""))
        }

        recyclerData.postValue(list)

    }


    fun getSearchUserList(query: String) {
        viewModelScope.launch(Dispatchers.IO) {

            userRepository.getUser(query).collect{
            }

        }
    }
    fun getAllUserSearchResult(){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getAllUser().collect {
                userList.postValue(it)
            }
        }
    }

    fun getTestUserSearchResult(){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getTestUserList().collect {

                testUserList.postValue(it)
            }
        }
    }

    //게시글 검색 서치뷰 리스너
    fun searchContentQueryTextListener() = object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            //검색 버튼 클릭시
            var emptyList = emptyList<String>()
            if(emptyList.isEmpty()){
                return true
            }else{
                getSearchUserList(query!!)
                return false
            }
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            //검색어 변경 시
            filteringContentList(newText!!)
            return true
        }

    }

    fun filteringContentList(newText: String){
        //newText 기반으로 유저 데이터 필터링
    }

    fun getSearchContentList() {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }

    //에딧 텍스트 TextWatcher
    fun textWatcher() = object :TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            edittextText.postValue(s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            edittextText.postValue(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            edittextText.postValue(s.toString())
        }

    }
    
    //채팅방 있는지 확인하고 있으면 snapshot id 가져오기
    fun checkChatRoom(destinationUid : String){
        val repository = ChatRepository()
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkChatRoom(destinationUid).collect {

                chatRoomUid.postValue(it)
            }
        }
    }

    //메세지 가져오기
    fun getMessageList(){
        val repository = ChatRepository()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getChat(chatRoomUid.value.toString()).collect {
                chatList.postValue(it)
            }
        }
    }

    fun createChatRoom(destinationUid: String){
        val repository = ChatRepository()
        var chatDTOs = ChatDTO()
        chatDTOs.users[auth.currentUser?.uid!!] = true;
        chatDTOs.commentTimestamp = System.currentTimeMillis()
        chatDTOs.users[destinationUid!!] = true
        viewModelScope.launch(Dispatchers.IO) {
            //채팅방 id가 없다면 채팅방 생성 후 메세지 전달
            if (chatRoomUid.value == null) {

                repository.createChatRoom(destinationUid, chatDTOs).collect {
                    if (it) println("채팅방 생성 성공") else println("채팅방 생성 실패")
                }
                //채팅방이 있다면 그냥 메세지 전달
            }
        }
    }

    //채팅 보내기
    fun sendMessage(destinationUid: String){
        val repository = ChatRepository()

        if (chatList.value!!.isNotEmpty()){
            chatList.value!!.clear()
        }

        var chatDTOs = ChatDTO()
        chatDTOs.users[auth.currentUser?.uid!!] = true;
        chatDTOs.commentTimestamp = System.currentTimeMillis()
        chatDTOs.users[destinationUid!!] = true
        viewModelScope.launch(Dispatchers.IO) {
            //채팅방 id가 없다면 채팅방 생성 후 메세지 전달
            if (chatRoomUid.value == null) {

                repository.createChatRoom(destinationUid,chatDTOs).collect {
                    if (it) println("채팅방 생성 성공") else println("채팅방 생성 실패")

                    //채팅방 생성하고 채팅방 uid 가져오기
                    checkChatRoom(destinationUid)

                    repository.checkChatRoom(destinationUid).collect{

                        //채팅방을 생성하고도 에딧텍스트에 값이 남아있다면 메세지 전달
                        if (edittextText.value!!.isNotEmpty()){
                            var comment = ChatDTO.Comment()
                            comment.uid = auth.currentUser!!.uid
                            comment.message = edittextText.value.toString()
                            comment.timestamp = System.currentTimeMillis()

                            repository.addChat(it.toString(),comment).collect {
                                if (it) println("채팅 저장 성공")  else println("채팅 저장 실패")
                                //채팅 다 보낸뒤 edittextText 교체해주기
                                edittextText.postValue(null)
                            }
                        }
                    }


                }

            //채팅방이 있다면 그냥 메세지 전달
            }else{

                var comment = ChatDTO.Comment()
                comment.uid = auth.currentUser!!.uid
                comment.message = edittextText.value.toString()
                comment.timestamp = System.currentTimeMillis()

                repository.addChat(chatRoomUid.value.toString(),comment).collect {
                    if (it) println("채팅 저장 성공")  else println("채팅 저장 실패")
                    //채팅 다 보낸뒤 edittextText 교체해주기
                    edittextText.postValue(null)
                }
            }
        }




    }

    //채팅방 목록 가져오기
    fun initMyChatRoomList(uid: String){
        viewModelScope.launch(Dispatchers.IO){
            chatRepository.getChatRoomList(uid).collect{
                chatRoomList.postValue(it)
            }
        }
    }

    //해당 유저의 친구 목록 가져오기
    fun initUserFriendsList(uid : String){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getFriendsList(uid).collect{
                println("가져온 친구 목록 = ${it.toString()}")
                it.forEach {
                    getUserData(it.uid.toString())
                }
            }
        }
    }



    //유저 정보 가져오기
    fun getUserData(uid : String){
        friendsListState.postValue("getting")
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getUser(uid).collect { userData ->
                println("가져온 유저 정보 = ${userData}")

                userRepository.getUserProfileImage(uid).collect{ userProfileImageUrl ->
                    println("가져온 유저 프로필 이미지 정보 = ${userProfileImageUrl}")

                    var arrayList = arrayListOf<RecyclerDefaultModel>()

                    if (userProfileImageUrl != null){
                        arrayList.add(
                            RecyclerDefaultModel(
                                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                                userProfileImageUrl,
                                userData.uid.toString(),
                                null,
                                userData.userName.toString(),
                                "임시 프로필 설명"
                            )
                        )
                    }else{
                        arrayList.add(
                            RecyclerDefaultModel(
                                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                                "",
                                userData.uid.toString(),
                                null,
                                userData.userName.toString(),
                                "임시 프로필 설명"
                            )
                        )
                    }




                    recyclerData.postValue(arrayList)
                    friendsListState.postValue("complete")
                }
            }
        }
    }


}