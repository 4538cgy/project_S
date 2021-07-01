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
import com.uos.smsmsm.data.*
import com.uos.smsmsm.repository.ChatRepository
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Singleton

// 채팅 / Timeline / 친구 찾기등 소셜 네트워크 기능 viewmodel
@Singleton
class SNSUtilViewModel @ViewModelInject constructor(
) : ViewModel(){
    companion object{
        //받아온 친구 리스트 저장
        var friendsList : ArrayList<RecyclerDefaultModel> = ArrayList<RecyclerDefaultModel>()
    }
    val userRepository : UserRepository by lazy{ UserRepository()}
    val chatRepository : ChatRepository by lazy{ChatRepository()}
    val contentRepository : ContentRepository by lazy { ContentRepository() }
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

    var timelineDataList = MutableLiveData<Map<String,ContentDTO>?>()

    val findUserByUserName : MutableLiveData<List<UserDTO?>> by lazy { MutableLiveData<List<UserDTO?>>() }

    var pagingcount = 1
    var list : ArrayList<String> = arrayListOf()

    val joblist : ArrayList<Job> = ArrayList<Job>()
    fun cancelAlljob(){
        if(joblist.size > 0) {
            joblist.forEach {
                if (!it.isCompleted) {
                    it.cancel()
                }
            }
            joblist.clear()
        }
    }
    fun timeLineDataListClear(){
        println("으아아 ${timelineDataList.value.toString()}")
        timelineDataList.value = null

        println("으아아2 ${timelineDataList.value.toString()}")
    }

    fun getData(){

            println("으아아 페이징 카운터 $pagingcount")
            println("리스트의 사이즈 ${list.size}")
            if (pagingcount < list.size) {
               val job = viewModelScope.launch(Dispatchers.Main) {
                    println("가져올 데이터 id ${list[pagingcount]}")
                    contentRepository.getContents(list[pagingcount]).collect { data ->
                        println("콜렉트 결과 ${data.toString()}")
                        timelineDataList.postValue(data)

                        pagingcount++
                    }

                }
                joblist.add(job)
            }else{
                println("게시글의 마지막입니다.")
            }


    }

    fun getTimeLineData(){
        println("으어어")
        var contents : MutableMap<String,ContentDTO> = HashMap()

        //#1 내 구독함과 내가 작성한 글 가져오기
       val job = viewModelScope.launch(Dispatchers.IO){


            contentRepository.getSubscribeContentsWithMyContents(auth.currentUser!!.uid).collect {contentIdList ->
                // 가져올 데이터가 아무것도 없을 경우에 대한 예외처리
                if(contentIdList == null || contentIdList.isEmpty()){

                }else {
                    list = contentIdList!!
                    contentIdList.forEach {
                        println("가져온 게시글 id  = ${it.toString()}")

                    }
                    getData()
                }

            }
        }
        joblist.add(job)

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
        val job = viewModelScope.launch(Dispatchers.IO) {

            userRepository.getUser(query).collect{
            }

        }
        joblist.add(job)
    }
    fun getAllUserSearchResult(){
        val job = viewModelScope.launch(Dispatchers.IO){
            userRepository.getAllUser().collect {
                userList.postValue(it)
            }
        }
        joblist.add(job)
    }

    fun getTestUserSearchResult(){
        val job = viewModelScope.launch(Dispatchers.IO){
            userRepository.getTestUserList().collect {

                testUserList.postValue(it)
            }
        }
        joblist.add(job)
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
        val job = viewModelScope.launch(Dispatchers.IO) {
            chatRepository.checkChatRoom(destinationUid).collect {

                chatRoomUid.postValue(it)
            }
        }
        joblist.add(job)
    }

    //메세지 가져오기
    fun getMessageList(){
        val job = viewModelScope.launch(Dispatchers.IO) {
            chatRepository.getChat(chatRoomUid.value.toString()).collect {
                chatList.postValue(it)
            }
        }
        joblist.add(job)
    }

    fun createChatRoom(destinationUid: String){
        var chatDTOs = ChatDTO()
        chatDTOs.users[auth.currentUser?.uid!!] = true;
        chatDTOs.commentTimestamp = System.currentTimeMillis()
        chatDTOs.users[destinationUid!!] = true
        val job = viewModelScope.launch(Dispatchers.IO) {
            //채팅방 id가 없다면 채팅방 생성 후 메세지 전달
            if (chatRoomUid.value == null) {

                chatRepository.createChatRoom(destinationUid, chatDTOs).collect {
                    if (it) println("채팅방 생성 성공") else println("채팅방 생성 실패")
                }
                //채팅방이 있다면 그냥 메세지 전달
            }
        }
        joblist.add(job)
    }

    //채팅 보내기
    fun sendMessage(destinationUid: String){

        if (chatList.value!!.isNotEmpty()){
            chatList.value!!.clear()
        }

        var chatDTOs = ChatDTO()
        chatDTOs.users[auth.currentUser?.uid!!] = true;
        chatDTOs.commentTimestamp = System.currentTimeMillis()
        chatDTOs.users[destinationUid!!] = true
        val job = viewModelScope.launch(Dispatchers.IO) {
            //채팅방 id가 없다면 채팅방 생성 후 메세지 전달
            if (chatRoomUid.value == null) {

                chatRepository.createChatRoom(destinationUid,chatDTOs).collect {
                    if (it) println("채팅방 생성 성공") else println("채팅방 생성 실패")

                    //채팅방 생성하고 채팅방 uid 가져오기
                    checkChatRoom(destinationUid)

                    chatRepository.checkChatRoom(destinationUid).collect{

                        //채팅방을 생성하고도 에딧텍스트에 값이 남아있다면 메세지 전달
                        if (edittextText.value!!.isNotEmpty()){
                            var comment = ChatDTO.Comment()
                            comment.uid = auth.currentUser!!.uid
                            comment.message = edittextText.value.toString()
                            comment.timestamp = System.currentTimeMillis()

                            chatRepository.addChat(it.toString(),comment).collect {
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

                chatRepository.addChat(chatRoomUid.value.toString(),comment).collect {
                    if (it) println("채팅 저장 성공")  else println("채팅 저장 실패")
                    //채팅 다 보낸뒤 edittextText 교체해주기
                    edittextText.postValue(null)
                }
            }
        }
        joblist.add(job)
    }

    //채팅방 목록 가져오기
    fun initMyChatRoomList(uid: String){
       val job = viewModelScope.launch(Dispatchers.IO){
            chatRepository.getChatRoomList(uid).collect{
                chatRoomList.postValue(it)
            }
        }
        joblist.add(job)
    }

    //해당 유저의 친구 목록 가져오기
    fun initUserFriendsList(uid : String){
       val job = viewModelScope.launch(Dispatchers.IO){
            userRepository.getFriendsList(uid).collect{
                println("가져온 친구 목록 = ${it.toString()}")
//                it.forEach {
//                    getUserData(it)
//                }
                getUserListData(it)
            }
        }
        joblist.add(job)
    }



    //유저 정보 가져오기 // 리스트로 가져오면서 필요 없어졌는데 혹시 몰라 사용할 수 있어 그냥 둠
    fun getUserData(uid: String) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser(uid).collect { userData ->
                println("가져온 유저 정보 = ${userData}")

                userRepository.getUserProfileImage(uid).collect { userProfileImageUrl ->
                    println("가져온 유저 프로필 이미지 정보 = ${userProfileImageUrl}")

                    val recyclerDefaultModel: RecyclerDefaultModel =
                        if (userProfileImageUrl != null) {
                            RecyclerDefaultModel(
                                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                                userProfileImageUrl,
                                userData.uid.toString(),
                                null,
                                userData.userName.toString(),
                                "임시 프로필 설명"
                            )
                        } else {
                            RecyclerDefaultModel(
                                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                                "",
                                userData.uid.toString(),
                                null,
                                userData.userName.toString(),
                                "임시 프로필 설명"
                            )
                        }
                    friendsList.add(recyclerDefaultModel)
                    initUserFriendsList(auth.currentUser!!.uid)
                }
            }
        }
        joblist.add(job)
    }
    // 리스트로 친구 정보 가져오는 함수 (횟수만큼 가져오면 결과 리턴)
    private fun getUserListData(uidList : ArrayList<SubscribeDTO.SubscribingDTO>){
        val finishCount = uidList.size
        val arrayList = arrayListOf<RecyclerDefaultModel>()
        if(uidList.isEmpty()){
            recyclerData.postValue(arrayList)
            friendsListState.postValue("complete")
        }else{
            friendsListState.postValue("getting")
        uidList.forEach {
         val job = viewModelScope.launch(Dispatchers.IO) {
                userRepository.getUser(it.uid!!).collect { userData ->
                    userRepository.getUserProfileImage(it.uid!!).collect { userProfileImageUrl ->

                        if (userProfileImageUrl != null) {
                            arrayList.add(
                                RecyclerDefaultModel(
                                    RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                                    userProfileImageUrl,
                                    userData.uid.toString(),
                                    null,
                                    userData.userName.toString(),
                                    "임시 프로필 설명",
                                    it.isFavorite
                                )
                            )
                        } else {
                            arrayList.add(
                                RecyclerDefaultModel(
                                    RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                                    "",
                                    userData.uid.toString(),
                                    null,
                                    userData.userName.toString(),
                                    "임시 프로필 설명",
                                    it.isFavorite
                                )
                            )
                        }

                        if (arrayList.size == finishCount) {
                            friendsList.clear()
                            friendsList.addAll(arrayList)
                            recyclerData.postValue(arrayList)
                            friendsListState.postValue("complete")
                        }
                    }

                }
            }
            joblist.add(job)
        }
        }

    }
    @ExperimentalCoroutinesApi
    fun getUserByUserName(userName: String) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserByUserName(userName).collect {
                findUserByUserName.postValue(it)
            }
        }
        joblist.add(job)
    }


}