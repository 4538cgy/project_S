package com.uos.smsmsm.fragment.customersupport

import android.os.Bundle
import android.view.View
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.lobby.LobbyActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.FaqDTO
import com.uos.smsmsm.databinding.FragmentFaqAnswerBinding

class FAQAnswerFragment(val faqItem : FaqDTO) : BaseFragment<FragmentFaqAnswerBinding>(R.layout.fragment_faq_answer ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fragment = this@FAQAnswerFragment
            answer = faqItem.answer
            question = faqItem.question
        }

    }
    fun close(v: View){
        (activity as LobbyActivity).popFragment(this)
    }

}