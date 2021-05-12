package com.uos.smsmsm.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.data.*
import com.uos.smsmsm.repository.BackgroundRepository
import com.uos.smsmsm.repository.ChatRepository
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// 채팅 / Timeline / 친구 찾기등 소셜 네트워크 기능 viewmodel
@Singleton
class SNSUtilViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,
                                                    var userRepository : UserRepository,
                                                    var chatRepository : ChatRepository,
                                                    var contentRepository : ContentRepository
) : ViewModel(){
    companion object{
        var friendsUidList : ArrayList<String> = ArrayList<String>()
    }
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

    val auth = FirebaseAuth.getInstance()

    var timelineDataList = MutableLiveData<Map<String,ContentDTO>>()

    val findUserByUserName : MutableLiveData<List<UserDTO?>> by lazy { MutableLiveData<List<UserDTO?>>() }

    var pagingcount = 0
    var list : ArrayList<String> = arrayListOf()

    fun getData(){

            println("으아아 페이징 카운터 $pagingcount")
            println("리스트의 사이즈 ${list.size}")
            if (pagingcount < list.size) {
                viewModelScope.launch(Dispatchers.Main) {
                    println("가져올 데이터 id ${list[pagingcount]}")
                    contentRepository.getContents(list[pagingcount]).collect { data ->
                        println("콜렉트 결과 ${data.toString()}")
                        timelineDataList.postValue(data)

                        pagingcount++
                    }

                }

            }else{
                println("게시글의 마지막입니다.")
            }


    }

    fun getTimeLineData(){
        println("으어어")
        var contents : MutableMap<String,ContentDTO> = HashMap()

        //#1 내 구독함과 내가 작성한 글 가져오기
        viewModelScope.launch(Dispatchers.IO){


            contentRepository.getSubscribeContentsWithMyContents(auth.currentUser!!.uid).collect {contentIdList ->
                list = contentIdList!!
                contentIdList.forEach {
                    println("가져온 게시글 id  = ${it.toString()}")
                }
                getData()
                /*
                if (contentIdList != null){
                    //#2 가져온 구독 게시글 리스트 대로 게시글 원본 가져오기
                    contentIdList.forEach { contentId ->
                        viewModelScope.launch {
                            contentRepository.getContents(contentId).collect {
                                contents.putAll(it)

                                if (contentIdList.size == contents.size){
                                    //#3 내 게시글 가져와서 contents에 넣고 timestamp를 기준으로 정렬하기
                                    viewModelScope.launch {
                                        contentRepository.getUserPostContent(auth.currentUser!!.uid).collect {
                                            contents.putAll(it)

                                            //#3-1 정렬
                                            var result = contents.toList().sortedByDescending { (_,value) -> value.timestamp }.toMap()
                                            //#4 완성된 게시글 원본 recyclerview에 연결하기
                                            println("뀨")
                                            timelineDataList.postValue(result)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }else{
                    viewModelScope.launch {
                        contentRepository.getUserPostContent(auth.currentUser!!.uid).collect {
                            contents.putAll(it)
                            //#3-1 정렬
                            var result = contents.toList().sortedByDescending { (_,value) -> value.timestamp }.toMap()
                            //#4 완성된 게시글 원본 recyclerview에 연결하기
                            println("꺄")
                            timelineDataList.postValue(result)
                        }
                    }
                }

                 */
            }
        }
        /*
         다음과 같이 수정해야함.
         #1. 리사이클러뷰에 꽂아넣어야할 list가 존재함.
         #2. list에 구독하고 있는 PostId의 원본을 넣어줌.
         #3. 원본을 넣어줬으면 내가 작성한 게시글을 넣어줌.
         #4. 정렬 함.
         */
    }




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
                friendsUidList.clear()
                friendsUidList.addAll(it)
                it.forEach {
                    getUserData(it)
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
    @ExperimentalCoroutinesApi
    fun getUserByUserName(userName: String) : Job =
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getUserByUserName(userName).collect {
                findUserByUserName.postValue(it)
            }
        }


}