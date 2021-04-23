package com.uos.smsmsm.ui.photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityPhotoViewBinding

class PhotoViewActivity : AppCompatActivity() {

    lateinit var binding : ActivityPhotoViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo_view)
        binding.activityphotoview = this@PhotoViewActivity

        Glide.with(binding.root.context).load(intent.getStringExtra("imageUrl")).into(binding.activityPhotoViewPhotoview)


    }

    fun backButtonPressed(view : View){ finish() }
}