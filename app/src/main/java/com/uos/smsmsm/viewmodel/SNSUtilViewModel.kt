package com.uos.smsmsm.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
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


    //테스트 목적
    var testUserList : MutableLiveData<ArrayList<UserDTO>> = MutableLiveData()
    //본목적
    var userList : MutableLiveData<ArrayList<UserDTO>> = MutableLiveData()

    //친구 목록 리스트의 상태
    var friendsListState : MutableLiveData<String> = MutableLiveData()

    val repository = UserRepository()

    val auth = FirebaseAuth.getInstance()



    //유저 검색 서치 뷰 리스너
    fun searchUserQueryTextListener() = object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            //검색 버튼 클릭시
            var emptyList = emptyList<String>()

            println("으아아아 $query")

            getSearchUserList(query!!)
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            //검색어 변경 시
            println("으아아아 $newText")
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

                println("SNS 뷰모델에서의 필터링된 유저 데이터 ${it.toString()}")

            }
        }
        searchUserResult.postValue(pathList)
        searchUserResultDataConvertRecyclerData()
        println("searchUserResult의 값입니다." +searchUserResult.value.toString())
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
        println("getSearchUserList 실행 ++++++++++++++++++++++++")
        viewModelScope.launch(Dispatchers.IO) {

            repository.getUser(query).collect{
                println(it.toString() + " 유저 검색 결과 입니다. ")
            }

        }
    }
    fun getAllUserSearchResult(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getAllUser().collect {
                userList.postValue(it)
            }
        }
    }

    fun getTestUserSearchResult(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getTestUserList().collect {

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
                println("결과가 없음")
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
            /*
            repository.getContentList().collect{
            }
             */
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
                println("으앙아ㅣ래캥랰앸 $it")

                chatRoomUid.postValue(it.toString())
                println("채팅방 확인중~~~~~~ ${chatRoomUid.value}")
            }
        }

        /*
        if (chatRoomUid.value == null){
            println("으아아 채팅방이 없습니다. 채팅방을 만들러갑니다.")
            createChatRoom(destinationUid)
        }
         */
    }

    //메세지 가져오기
    fun getMessageList(){
        println("메세지 가져오기")
        val repository = ChatRepository()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getChat(chatRoomUid.value.toString()).collect {
                println("가져온 메세지 ${it.toString()}")
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
    /*
    Q: 뷰에서 sendMessage를 onClick으로 호출할 때 아래와 같은 인자값들을 어떻게 전달합니까?
    Sub Q: 혹시 이 인자들을 호출시에 전달하지않고 ChatRoomFragment 혹은 메세지를 전달해야하는 View에서 라이브데이터에 이 인자들의 값을 넣어두고 꺼내올 수 있습니까?
     */
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
    //채팅방 목록 최초 초기화
    //호출 후 recyclerView 반드시 notifyChange
    fun initChatRoomList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        list.add(
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2045%EC%B4%88_.png?alt=media&token=b15104e7-ed95-4acd-8f41-c9890af2a5ec",
                "aaa",
                null,
                "아 적기 힘들다",
                "아 뭐적어야함"
            )
        )

        recyclerData.postValue(list)
    }

    //채팅 목록 가져오기
    //채팅 목록 최초 초기화
    //호출 후 recyclerView 반드시 notifyChange
    fun initChatList(){
        var list : ArrayList<RecyclerDefaultModel> = arrayListOf()
        getMessageList()
        recyclerData.postValue(list)
    }

    //해당 유저의 친구 목록 가져오기
    fun initUserFriendsList(uid : String){
        viewModelScope.launch(Dispatchers.IO){
            repository.getFriendsList(uid).collect{
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
            repository.getUser(uid).collect {userData ->
                println("가져온 유저 정보 = ${userData}")

                repository.getUserProfileImage(uid).collect{userProfileImageUrl ->
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