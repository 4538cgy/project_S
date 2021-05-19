package com.uos.smsmsm.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// 사용자 로그인 / Setting 등 사용자 정보 관련 ViewModel
class UserUtilViewModel @ViewModelInject constructor() : ViewModel(){

    var currentUser : MutableLiveData<UserDTO> = MutableLiveData()
    var currentDestinationUser : MutableLiveData<UserDTO> = MutableLiveData()

    //ProfileActivity - 닉네임
    var userName : MutableLiveData<String> = MutableLiveData()
    //ProfileActivity - 유저 사진
    var profileImage : MutableLiveData<String> = MutableLiveData()
    //친구 추가에 성공했는지 판단
    var isSuccessAddFirends : MutableLiveData<String> = MutableLiveData()

    var checkFriends = MutableLiveData<Boolean>()

    val userRepository  = UserRepository()

    val auth = FirebaseAuth.getInstance()
    val isFavoriteResult : MutableLiveData<RecyclerDefaultModel?> by lazy {
        MutableLiveData<RecyclerDefaultModel?>()
    }

    fun checkFriend(destinationUid: String){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.isFriend(auth.currentUser!!.uid, destinationUid).collect{
                println("으아아아아아 $it")
                checkFriends.postValue(it)
            }
        }
    }

    fun addFriend(destinationUid: String){
        viewModelScope.launch(Dispatchers.IO){

            userRepository.addFriend(auth.currentUser!!.uid,destinationUid).collect{
                
                //친구 추가에 성공했으면 친구인지 아닌지 판별
                //checkFriend(destinationUid)
                println("친구 추가 결과 ${it.toString()}")
                isSuccessAddFirends.postValue(it)
            }
        }
    }


    fun getUserName(destinationUid: String){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getUserNickName(destinationUid).collect{
                userName.postValue(it)
            }
        }
    }

    fun getUserProfile(destinationUid: String){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getUserProfileImage(destinationUid).collect {
                    profileImage.postValue(it)
            }
        }
    }


    fun initDestinationUser(destinationUid : String){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.getUser(destinationUid).collect {
                currentUser
            }
        }
    }

    // 친구 즐겨찾기 요청
    fun requestSetFavorite(uid : String, isFavorite : Boolean): Job =
        viewModelScope.launch(Dispatchers.IO){
            userRepository.requestFavorite(uid,isFavorite).collect {
                if(it == "Success"){
                    for(i in SNSUtilViewModel.friendsList){
                        if(i.uid == uid){
                            i.isFavorite = isFavorite

                            isFavoriteResult.postValue(i)
                            break
                        }
                    }

                }else if(it == "Failure"){
                    isFavoriteResult.postValue(null)
                }
            }
        }
}