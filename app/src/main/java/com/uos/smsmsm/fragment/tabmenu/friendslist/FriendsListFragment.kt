package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.friendslistsetting.FriendsListSettingActivity
import com.uos.smsmsm.activity.search.SearchFriendActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentFriendsListBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter
import com.uos.smsmsm.ui.bottomsheet.BottomSheetDialogAddFriends
import com.uos.smsmsm.util.dialog.LoadingDialog
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsListFragment : Fragment() {

    lateinit var binding: FragmentFriendsListBinding
    private val viewmodel: SNSUtilViewModel by viewModels()

    private val auth = FirebaseAuth.getInstance()
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends_list, container, false)
        binding.fragmentfriendslist = this

        //프로그레스 초기화
        loadingDialog = LoadingDialog(binding.root.context)
        //프로그레스 투명하게
        loadingDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //프로그레스 꺼짐 방지
        loadingDialog!!.setCancelable(false)

        //친구목록 가져오기
        /*
        다음과 같이 추가해야함
        - 가져온 친구 목록을 내부 데이터에 저장할것
        - 친구목록 동기화 버튼을 누를시에 데이터를 새로 가져와서 내부데이터에 저장할것
        - 친구목록 내부데이터가 비어있을시에만 친구 목록을 가져올것 [ 최초 1회 실행 ]
        - 해당 activity가 실행되면 친구 목록 데이터가 갱신된 시간을 체크하여 내부데이터를 업데이트 해줄것
         */

        viewmodel.initUserFriendsList(auth.currentUser!!.uid)


        //friends List의 상태 확인

        viewmodel.friendsListState.observe(viewLifecycleOwner, Observer {
            loadingDialog.show()
            when(it){
                //데이터가 읽히는 중이면 실행
                "getting" ->{ binding.fragmentFriendsListTextviewNotice.visibility = View.VISIBLE
                binding.fragmentFriendsListTextviewNotice.text = "데이터 불러오는 중" }

                //데이터 읽은 후에 데이터 내용에 따라 내용 출력  #dialog와 같은것으로 표시를 바꿔주면 더욱 이뻐질듯함
                "complete" -> {
                    if (viewmodel.recyclerData.value!!.isEmpty()){ binding.fragmentFriendsListTextviewNotice.visibility = View.VISIBLE
                    binding.fragmentFriendsListTextviewNotice.setText(R.string.notice_no_friends)
                        loadingDialog.dismiss()
                    }else{
                        binding.fragmentFriendsListTextviewNotice.visibility = View.GONE
                        loadingDialog.dismiss()
                    }
                }
            }
        })

        initRecyclerViewAdapter()
        loadingDialog.dismiss()
        return binding.root
    }
    fun initRecyclerViewAdapter(){
        var data = MutableLiveData<ArrayList<RecyclerDefaultModel>>()

        val recyclerObserver : Observer<ArrayList<RecyclerDefaultModel>>
                = Observer { livedata ->
            data.value = livedata

            binding.fragmentFriendsListRecycler.adapter = MultiViewTypeRecyclerAdapter(binding.root.context,data)
            binding.fragmentFriendsListRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        }

        viewmodel.recyclerData.observe(viewLifecycleOwner, recyclerObserver)




    }
    fun openSearhActivity(view: View) {
        startActivity(Intent(binding.root.context, SearchFriendActivity::class.java))
    }

    fun addFriend(view : View){
        val bottomSheetDialog = BottomSheetDialogAddFriends()
        bottomSheetDialog.show(requireActivity().supportFragmentManager,"wow")
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
