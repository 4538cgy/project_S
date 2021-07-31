package com.uos.smsmsm.fragment.customersupport

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.PropensityAnalysisDTO
import com.uos.smsmsm.databinding.FragmentPropensityAnalysisBinding
import com.uos.smsmsm.recycleradapter.customersupport.PropensityAnalysisAdapter
import com.uos.smsmsm.viewmodel.AppUtilViewModel

class PropensityAnalysisFragment : BaseFragment<FragmentPropensityAnalysisBinding>(R.layout.fragment_propensity_analysis)  {
    private val TAG : String  = "PropensityAnalysisFragment"
    private val questionlist : ArrayList<PropensityAnalysisDTO> = ArrayList()
    private val appUtilViewModel : AppUtilViewModel = AppUtilViewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        questionlist.add(PropensityAnalysisDTO(0,"가가가가가"))
        questionlist.add(PropensityAnalysisDTO(1,"나나나나나"))
        questionlist.add(PropensityAnalysisDTO(2,"다다다다다"))
        questionlist.add(PropensityAnalysisDTO(3,"라라라라라"))
        binding.propensityAnalysisRecyclerview.adapter = PropensityAnalysisAdapter(requireContext(),appUtilViewModel)
        binding.questionList = this.questionlist
        appUtilViewModel.setScoreList(this.questionlist.size)

        appUtilViewModel.propensityAnalysisScore.observe(this, Observer {
            // 스코어 총합 반환
        })
    }
    fun close(v: View) {
        (activity as LobbyActivity).popFragment(this)
    }

}