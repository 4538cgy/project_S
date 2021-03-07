package com.uos.smsmsm.fragment.tabmenu.timeline

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.FragmentTimeLineBinding

class TimeLineFragment : Fragment() {

    lateinit var binding: FragmentTimeLineBinding
    private var isOpenFAB = false

    companion object { // var -> const val
        const val PICK_PROFILE_FROM_ALBUM = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // View? -> View (NonNull)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_line, container, false)
        binding.fragmenttimeline = this

        return binding.root
    }

    fun uploadPhoto(view: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
    }

    fun takePhotoCamera(view: View) {}

    fun takePhotoGallery(view: View) {}

    fun writeContent(view: View) {}

    fun clickFab(view: View) {
        isOpenFAB = if (!isOpenFAB) {
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabGallery, "translationY", -600f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabWritePost, "translationY", -400f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabCamera, "translationY", -200f)
                .apply { start() }
            true
        } else {
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabGallery, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabWritePost, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabCamera, "translationY", -0f)
                .apply { start() }
            false
        }
    }
}
