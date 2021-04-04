package com.uos.smsmsm.activity.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ItemUploadImageViewBinding
import com.uos.smsmsm.util.GalleryUtil.MediaItem

class UploadImageFragment (private var mediaItem : MediaItem): Fragment(){

    private lateinit var binding : ItemUploadImageViewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.item_upload_image_view, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.itemUploadImageViewImg.setImageURI(mediaItem.contentUri)
    }

    override fun onResume() {
        super.onResume()
    }
    fun updateItem(mediaItem: MediaItem){
        this.mediaItem = mediaItem

        binding.itemUploadImageViewImg.setImageURI(mediaItem.contentUri)
    }
}