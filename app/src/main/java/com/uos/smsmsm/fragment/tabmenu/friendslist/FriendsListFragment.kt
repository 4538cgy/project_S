package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.friendslistsetting.FriendsListSettingActivity
import com.uos.smsmsm.activity.search.SearchFriendActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentFriendsListBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsListFragment : Fragment() {

    lateinit var binding: FragmentFriendsListBinding
    private val viewmodel: SNSUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends_list, container, false)
        binding.fragmentfriendslist = this

        //친구목록 가져오기
        /*
        다음과 같이 추가해야함
        - 가져온 친구 목록을 내부 데이터에 저장할것
        - 친구목록 동기화 버튼을 누를시에 데이터를 새로 가져와서 내부데이터에 저장할것
        - 친구목록 내부데이터가 비어있을시에만 친구 목록을 가져올것 [ 최초 1회 실행 ]
        - 해당 activity가 실행되면 친구 목록 데이터가 갱신된 시간을 체크하여 내부데이터를 업데이트 해줄것
         */
        viewmodel.initUserFriendsList(auth.currentUser!!.uid)

        initRecyclerViewAdapter()

        return binding.root
    }
    fun initRecyclerViewAdapter(){
        var data = MutableLiveData<ArrayList<RecyclerDefaultModel>>()

        //친구 목록이 비어 있으면 친구 추가 안내 메세지를 출력
        if (data.value == null){ binding.fragmentFriendsListTextviewNotice.visibility = View.VISIBLE }

        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
                = Observer { livedata ->
            data.value = livedata

            //친구 목록이 비어 있지않으면 친구 추가 안내 메세지 없애기
            if (data.value != null){ binding.fragmentFriendsListTextviewNotice.visibility = View.GONE }

            binding.fragmentFriendsListRecycler.adapter = MultiViewTypeRecyclerAdapter(binding.root.context,data)
            binding.fragmentFriendsListRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        }

        viewmodel.recyclerData.observe(viewLifecycleOwner, recyclerObserver)

    }
    fun openSearhActivity(view: View) {
        startActivity(Intent(binding.root.context, SearchFriendActivity::class.java))
    }

    fun openFriendListSettingActivity(view: View) {
        startActivityForResult(
            (
                    Intent(
                        binding.root.context,
                        FriendsListSettingActivity::class.java
                    )
                    ),
            1
        )
    }
}
