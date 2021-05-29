package com.uos.smsmsm.activity.friendfind

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Observer
import com.uos.smsmsm.R
import com.uos.smsmsm.base.BaseActivity
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.ActivityFindFriendsByIdBinding
import com.uos.smsmsm.recycleradapter.friends.find.FindFriendAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class FriendFindByIdActivity : BaseActivity<ActivityFindFriendsByIdBinding>(R.layout.activity_find_friends_by_id){

    private val snsViewModel : SNSUtilViewModel by viewModels()
    private val userUtilViewModel : UserUtilViewModel by viewModels()
    private val findFriendsList: ObservableArrayList<UserDTO> by lazy {
        ObservableArrayList<UserDTO>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.slide_up, R.anim.slide_up_out)
        binding.apply {
            activity = this@FriendFindByIdActivity
            recyclerFindFriendById.adapter = FindFriendAdapter(this@FriendFindByIdActivity,userUtilViewModel)
            friendlist = findFriendsList
        }

        snsViewModel.findUserByUserName.observe(this, Observer {
            it?.let {list ->
                if(list.isNotEmpty()) {
                    findFriendsList.clear()
                    findFriendsList.addAll(list)
                }
            } ?: {

            }()
        })
        userUtilViewModel.isSuccessAddFirends.observe(this, Observer {
            Log.d("TEST","it: $it")
            Log.d("TEST","it contains ${it.contains("SUBSCRIBE",true)}")
            if(it.contains("SUBSCRIBE",true)){
                Toast.makeText(baseContext, "친구추가 성공",Toast.LENGTH_LONG).show()
                finish()
            }
        })
        binding.editFindFriendById.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchUserByUserName()
                true
            }
            false
        }
        binding.imgFindFriendByIdSearch.setOnClickListener {
            searchUserByUserName()
        }

    }
    private fun searchUserByUserName(){
        snsViewModel.getUserByUserName(binding.editFindFriendById.text.toString())
    }

    override fun finish() {
        super.finish()

        overridePendingTransition(R.anim.slide_down, R.anim.slide_down_out)
    }
    fun close(v: View){
        finish()
    }



}