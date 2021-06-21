package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.friendslistsetting.FriendsListSettingActivity
import com.uos.smsmsm.activity.profile.ProfileActivity
import com.uos.smsmsm.activity.search.SearchFriendActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentFriendsListBinding
import com.uos.smsmsm.fragment.profile.ProfileFragment
import com.uos.smsmsm.fragment.tabmenu.timeline.TimeLineFragment
import com.uos.smsmsm.recycleradapter.friends.list.FriendListAdapter
import com.uos.smsmsm.ui.bottomsheet.BottomSheetDialogAddFriends
import com.uos.smsmsm.util.Delegate
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FriendsListFragment : BaseFragment<FragmentFriendsListBinding>(R.layout.fragment_friends_list) {

    private val auth = FirebaseAuth.getInstance()
    private var data = MutableLiveData<ArrayList<RecyclerDefaultModel>>()
    // 친구 리스트 adapter
    private val listAdapter : FriendListAdapter by lazy {
        FriendListAdapter(object : Delegate.Action1<RecyclerDefaultModel>{
            override fun run(item: RecyclerDefaultModel) {
                // 친구 삭제 액
                userViewModel.removeFriend(item.uid!!)
            }

        })
    }
    //즐겨찾기 친구 리스트 adapter
    private val favoriteAdapter : FriendListAdapter by lazy {
        FriendListAdapter(object : Delegate.Action1<RecyclerDefaultModel>{
            override fun run(item: RecyclerDefaultModel) {
                // 친구 삭제 액
                userViewModel.removeFriend(item.uid!!)
            }

        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentfriendslist = this

        //친구목록 가져오기
        /*
        다음과 같이 추가해야함
        - 가져온 친구 목록을 내부 데이터에 저장할것
        - 친구목록 동기화 버튼을 누를시에 데이터를 새로 가져와서 내부데이터에 저장할것
        - 친구목록 내부데이터가 비어있을시에만 친구 목록을 가져올것 [ 최초 1회 실행 ]
        - 해당 activity가 실행되면 친구 목록 데이터가 갱신된 시간을 체크하여 내부데이터를 업데이트 해줄것
         */

        snsViewModel.initUserFriendsList(auth.currentUser!!.uid)

        // 내 프로필 셋팅
        userViewModel.getUserProfile(auth.currentUser!!.uid.toString())
        userViewModel.profileImage.observe(viewLifecycleOwner, Observer {
            Glide.with(binding.root.context).load(it).apply(RequestOptions().circleCrop()).into(binding.fragmentFriendsListMyProfileTitleContentImageview) })

        //유저 닉네임 가져오기
        userViewModel.getUserName(auth.currentUser!!.uid.toString())
        userViewModel.userName.observe(viewLifecycleOwner, Observer { binding.fragmentFriendsListMyProfileTitleContentTextviewTitle.text = it.toString()})
        //friends List의 상태 확인

        snsViewModel.friendsListState.observe(viewLifecycleOwner, Observer {
            loadingDialog.show()
            when (it) {
                //데이터가 읽히는 중이면 실행
                "getting" -> {
                    binding.fragmentFriendsListTextviewNotice.visibility = View.VISIBLE
                    binding.fragmentFriendsListTextviewNotice.text = "데이터 불러오는 중"
                }

                //데이터 읽은 후에 데이터 내용에 따라 내용 출력  #dialog와 같은것으로 표시를 바꿔주면 더욱 이뻐질듯함
                "complete" -> {
                    if (snsViewModel.recyclerData.value != null) {
                        if (snsViewModel.recyclerData.value!!.isEmpty()) {
                            binding.fragmentFriendsListTextviewNotice.visibility = View.VISIBLE
                            binding.fragmentFriendsListFavoritesLayout.visibility = View.GONE
                            binding.fragmentFriendsListNormalListLayout.visibility = View.GONE
                            binding.fragmentFriendsListTextviewNotice.setText(R.string.notice_no_friends)
                            loadingDialog.dismiss()
                        } else {
                            binding.fragmentFriendsListNormalListLayout.visibility = View.VISIBLE
                            binding.fragmentFriendsListTextviewNotice.visibility = View.GONE
                            loadingDialog.dismiss()
                        }
                    }
                }
            }
        })
        // 내 프로필 클릭 시 내 프로필 상세 노출
        binding.fragmentFriendsListMyProfileLayout.setOnClickListener {
            /*
            Intent(binding.root.context, ProfileActivity::class.java).run {
                putExtra("uid",auth.currentUser!!.uid)
                binding.root.context.startActivity(this)
            }
            프라그먼트 교체로 변경되었음
             */
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.activity_lobby_fragmelayout,ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("uid",auth.currentUser!!.uid)
                }
            })
                .commit()
        }
        initRecyclerViewAdapter()
        loadingDialog.dismiss()
    }
    private fun initRecyclerViewAdapter(){
        binding.fragmentFriendsListRecycler.adapter = listAdapter
        binding.fragmentFriendsListFavoritesRecycler.adapter = favoriteAdapter
        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
                = Observer { livedata ->
            data.value = livedata
            listAdapter.replaceList(livedata)

            // 친구 리스트 중 즐겨찾기 리스트
            val favoriteList = ArrayList<RecyclerDefaultModel>()
            for( i in livedata){
                if(i.isFavorite != null && i.isFavorite){
                    favoriteList.add(i)
                }
            }

            if(favoriteList.isNotEmpty()){
                binding.fragmentFriendsListFavoritesLayout.visibility = View.VISIBLE
                favoriteAdapter.replaceList(favoriteList)
            }else{
                binding.fragmentFriendsListFavoritesLayout.visibility = View.GONE
            }

        }

        snsViewModel.recyclerData.observe(viewLifecycleOwner, recyclerObserver)


    }
    fun openSearchActivity(view: View) {
        startActivity(Intent(binding.root.context, SearchFriendActivity::class.java))
    }

    fun addFriend(view: View){
        val bottomSheetDialog = BottomSheetDialogAddFriends()
        bottomSheetDialog.show(requireActivity().supportFragmentManager, "wow")
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
