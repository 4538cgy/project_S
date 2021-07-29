package com.uos.smsmsm.fragment.customersupport

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.FaqDTO
import com.uos.smsmsm.databinding.FragmentFaqBinding
import com.uos.smsmsm.recycleradapter.customersupport.FaqAdapter
import com.uos.smsmsm.util.Delegate
import com.uos.smsmsm.viewmodel.AppUtilViewModel
import kotlinx.android.synthetic.main.fragment_faq.*

class FAQFragment(val type: String?) : BaseFragment<FragmentFaqBinding>(R.layout.fragment_faq) {
    private val TAG: String = "FAQFragment"
    private val faqlist: ArrayList<FaqDTO> = ArrayList<FaqDTO>()
    private val appUtilViewModel : AppUtilViewModel = AppUtilViewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@FAQFragment
            faqlist = this@FAQFragment.faqlist
        }
        binding.faqRecyclerview.adapter = FaqAdapter(rootContext, callback)
        appUtilViewModel.faqListLiveData.observe(this, Observer {
            faqlist.clear()
            faqlist.addAll(it)
            binding.run {
                faqlist = this@FAQFragment.faqlist
                if(it.size == 0 ){
                    faqRecyclerview.visibility = View.GONE
                    faqNoFaqItemText.visibility = View.VISIBLE
                }else{
                    faqRecyclerview.visibility = View.VISIBLE
                    faqNoFaqItemText.visibility = View.GONE
                }
            }
        })
        appUtilViewModel.requestGetFaq(type)
    }

    private val callback: Delegate.Action1<FaqDTO> = object : Delegate.Action1<FaqDTO> {
        override fun run(t1: FaqDTO) {
            if (t1.hasanswer) {
                (activity as LobbyActivity).pushFragment(FAQAnswerFragment(t1))
            } else {
                (activity as LobbyActivity).pushFragment(FAQFragment(t1.childuk))
            }
        }
    }

    fun close(v: View) {
        (activity as LobbyActivity).popFragment(this)
    }
}