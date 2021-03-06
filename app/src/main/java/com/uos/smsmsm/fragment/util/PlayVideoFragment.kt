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
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.databinding.FragmentVideoPlayBinding

class PlayVideoFragment(val uri: Uri) : BaseFragment<FragmentVideoPlayBinding>(R.layout.fragment_video_play) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentVideoPlayVideoView.apply {
            setMediaController(
                android.widget.MediaController(
                    requireContext()
                )
            )
            setVideoURI(uri)
            start()
        }
        binding.fragmentVideoPlayCloseBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                remove(this@PlayVideoFragment)
                commit()
            }
        }

    }

    override fun onDetach() {
        binding.fragmentVideoPlayVideoView.run{
            if(isPlaying){
                pause()
            }
            stopPlayback()
        }
        super.onDetach()
    }

}