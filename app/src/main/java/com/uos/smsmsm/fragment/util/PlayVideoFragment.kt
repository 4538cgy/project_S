package com.uos.smsmsm.fragment.util

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentVideoPlayBinding

class PlayVideoFragment(val uri: Uri) : Fragment(){
    lateinit var binding: FragmentVideoPlayBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_play, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentVideoPlayVideoView.setMediaController(
            android.widget.MediaController(
                requireContext()
            )
        )
        binding.fragmentVideoPlayVideoView.setVideoURI(uri)
        binding.fragmentVideoPlayVideoView.start()

        binding.fragmentVideoPlayCloseBtn.setOnClickListener {
            val mFragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val mFragmentTransaction: FragmentTransaction = mFragmentManager.beginTransaction()
            mFragmentTransaction.remove(this)
            mFragmentTransaction.commit()
        }

    }

    override fun onDetach() {
        if(binding.fragmentVideoPlayVideoView.isPlaying)
        binding.fragmentVideoPlayVideoView.pause()
        binding.fragmentVideoPlayVideoView.stopPlayback()
        super.onDetach()
    }

}