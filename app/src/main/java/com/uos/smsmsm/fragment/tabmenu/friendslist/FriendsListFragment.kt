package com.uos.smsmsm.fragment.tabmenu.friendslist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.friendslistsetting.FriendsListSettingActivity
import com.uos.smsmsm.activity.search.SearchFriendActivity
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.databinding.FragmentFriendsListBinding
import com.uos.smsmsm.recycleradapter.MultiViewTypeRecyclerAdapter

class FriendsListFragment : Fragment() {

    lateinit var binding: FragmentFriendsListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends_list, container, false)
        binding.fragmentfriendslist = this
        // ArrayList -> List (array -> collection)
        val list2 = listOf(
            RecyclerDefaultModel(
                RecyclerDefaultModel.TEXT_TYPE,
                "",
                null,
                "즐겨찾기",
                ""
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
                null,
                "FD_양꼬치와칭따오",
                "양꼬치 같이드실분"
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2048%EC%B4%88_.png?alt=media&token=d5429bc4-bee5-4be8-a7e6-82e3989913fe",
                null,
                "퀸더블랙",
                ""
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.TEXT_TYPE,
                "",
                null,
                "친구 62",
                ""
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2000%EC%B4%88_.png?alt=media&token=32d9aa22-3b5b-4d2d-a3da-dc65b783d576",
                null,
                "FS_로뎅",
                "22살 서울시 부산구 동대문동 서식중"
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2007%EC%B4%88_.png?alt=media&token=f551c7b1-04dc-4ffb-a0d1-b80509ec153a",
                null,
                "MS 24/7",
                "캬ㅕ캬컄캬캬컄ㅋ"
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2052%EB%B6%84%2004%EC%B4%88_.png?alt=media&token=fd6e1bb5-b45a-4528-a292-aa48189538dd",
                null,
                "내다릴봐예뿌짜낭",
                ""
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2051%EB%B6%84%2021%EC%B4%88_.png?alt=media&token=5cf7b0b8-7920-4394-a047-635c15e417e5",
                null,
                "으컁컁",
                "으캬캬캬컄ㅇ컁컁ㅋㅇ"
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2055%EB%B6%84%2026%EC%B4%88_.png?alt=media&token=bcc4d7dc-b58e-43e3-8b96-08b14f20f2ea",
                null,
                "노예구함",
                "이쁜이구해용~"
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2034%EC%B4%88_.png?alt=media&token=7de3d26f-59ce-44da-a588-a0e4728140c5",
                null,
                "24세빻돔",
                "뭘봐 시벌"
            ),
            RecyclerDefaultModel(
                RecyclerDefaultModel.FRIENDS_LIST_TYPE_TITLE_CONTENT,
                "https://firebasestorage.googleapis.com/v0/b/project-s-8efd0.appspot.com/o/TestImage%2FTEST_IMAGE_2021%EB%85%84%2002%EC%9B%94%2028%EC%9D%BC%20%EC%98%A4%ED%9B%84%2003%EC%8B%9C%2054%EB%B6%84%2045%EC%B4%88_.png?alt=media&token=b15104e7-ed95-4acd-8f41-c9890af2a5ec",
                null,
                "아 적기 힘들다",
                "아 뭐적어야함"
            ),

            )

        // binding.fragmentFriendsListRecycler.addItemDecoration(DividerItemDecoration(binding.root.context,DividerItemDecoration.VERTICAL))
        binding.fragmentFriendsListRecycler.adapter =
            MultiViewTypeRecyclerAdapter(binding.root.context, list2)
        binding.fragmentFriendsListRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        return binding.root
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
