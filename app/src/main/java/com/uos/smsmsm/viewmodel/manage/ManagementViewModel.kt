package com.uos.smsmsm.viewmodel.manage

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ManagementViewModel @ViewModelInject constructor() : ViewModel(){

    val repository = UserRepository()

    fun createUser() {

        val list = arrayListOf<UserDTO>()

        list.add(UserDTO("01012346789",true,"23949fnifnisdfnoisd",1231235345354,1,100,"cgy"))

        list.forEach {
            viewModelScope.launch(Dispatchers.IO) {
                repository.createTestUser(userData = it).collect {
                    println("create result $it")
                }
            }
        }
    }
}