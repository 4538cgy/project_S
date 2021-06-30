package com.uos.smsmsm.fragment.findfriends

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.UserDTO
import com.uos.smsmsm.databinding.FragmentFindFriendsByIdBinding
import com.uos.smsmsm.recycleradapter.friends.find.FindFriendAdapter
import com.uos.smsmsm.util.Delegate
import com.uos.smsmsm.util.workmanager.SubscribeWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendFindByIdFragment : BaseFragment<FragmentFindFriendsByIdBinding>(R.layout.fragment_find_friends_by_id){

    private val findFriendsList: ObservableArrayList<UserDTO> by lazy {
        ObservableArrayList<UserDTO>()
    }
    private var destinationUid: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            fragment = this@FriendFindByIdFragment
            recyclerFindFriendById.adapter = FindFriendAdapter(rootContext, object : Delegate.Action1<String>{
                override fun run(t1: String) {
                    destinationUid = t1
                    userViewModel.addFriend(t1)
                }

            })
            friendlist = findFriendsList
        }

        snsViewModel.findUserByUserName.observe(viewLifecycleOwner, findUserByUserNameObserver)
        userViewModel.isSuccessAddFirends.observe(viewLifecycleOwner, Observer {
            Log.d("TEST","it: $it")
            Log.d("TEST","it contains ${it.contains("SUBSCRIBER",true)}")
            if(it.contains("SUBSCRIBER",true)){
                Toast.makeText(rootContext, "친구추가 성공",Toast.LENGTH_LONG).show()
                userViewModel.isSuccessAddFirends.value = ""
                onSubscribeWorker()
                snsViewModel.getUserData(destinationUid!!)
                (activity as LobbyActivity).popFragment(this)
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

    private val findUserByUserNameObserver: Observer<List<UserDTO?>> = Observer<List<UserDTO?>> {
        it?.let { list ->
            if (list.isNotEmpty()) {
                findFriendsList.clear()
                findFriendsList.addAll(list)
                snsViewModel.findUserByUserName.value = null
            }
        }
    }
    override fun onDestroyView() {
        userViewModel.isSuccessAddFirends.removeObservers(viewLifecycleOwner)
        snsViewModel.findUserByUserName.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private fun searchUserByUserName(){
        snsViewModel.getUserByUserName(binding.editFindFriendById.text.toString())
    }

    fun close(v: View){
        (activity as LobbyActivity).popFragment(this)
    }
    fun onSubscribeWorker() {
        if(destinationUid != null) {
            println("백그라운드 실행")
            var data: MutableMap<String, Any> = HashMap()

            data.put("WORK_STATE", SubscribeWorker.WORK_COPY_PASTE_CONTENTS)
            data.put("WORK_DESTINATION_UID", destinationUid.toString())

            val inputData = Data.Builder().putAll(data).build()

            val uploadManager: WorkRequest =
                OneTimeWorkRequestBuilder<SubscribeWorker>().setInputData(inputData).build()
            WorkManager.getInstance(rootContext).enqueue(uploadManager)
        }
    }


}