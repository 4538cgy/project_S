package com.uos.smsmsm.viewmodel

import android.content.Intent
import android.provider.MediaStore
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


//게시글 업로드 , 게시글 다운로드 , 사진 업로드, 사진 다운로드 등등 Content와 관련된 모든 기능
class ContentUtilViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle) : ViewModel(){


    fun openGallery() : Intent{
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    fun openCamera() : Intent{
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }


}