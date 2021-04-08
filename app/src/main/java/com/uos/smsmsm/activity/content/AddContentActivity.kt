package com.uos.smsmsm.activity.content

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroupOverlay
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.ActivityAddContentBinding
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.GalleryUtil.MediaItem
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContentActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddContentBinding
    private val viewModel: ContentUtilViewModel by viewModels()
    private var isSelectImgCount : Int = 0
    private var uploadImageList = ArrayList<MediaItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_content)
        binding.lifecycleOwner = this
        binding.activityaddcontent = this
        binding.viewmodel = viewModel

        binding.activityAddContentGalleryRecyclerView.layoutManager = GridLayoutManager(applicationContext ,3 )

        binding.activityAddContentAddImageViewPager.adapter = UploadImageSlidePagerAdapter(this, uploadImageList)
    }

    fun openGallery(view :View) {

        if (!viewModel.galleryItems.hasObservers()) {
            viewModel.galleryItems.observe(this, androidx.lifecycle.Observer { it ->
                Log.d("Test", "size : ${it.size}")
                if(binding.activityAddContentGallery.visibility == View.GONE ) {
                    binding.activityAddContentGalleryRecyclerView.adapter =
                        GalleryAdapter(it, applicationContext,clickListener)
                }else{
                    (binding.activityAddContentGalleryRecyclerView.adapter as GalleryAdapter).galleryLists = it
                    (binding.activityAddContentGalleryRecyclerView.adapter as GalleryAdapter).notifyDataSetChanged()
                }

                binding.activityAddContentGallery.visibility = View.VISIBLE

            })
        }
        if(binding.activityAddContentGallery.visibility == View.GONE) {
            viewModel.getGallery()
        }

    }

    fun takePicture(view: View) {
        binding.activityAddContentGallery.visibility = View.GONE
    }
    private val clickListener  = View.OnClickListener {

        val clickViewHolder =
            binding.activityAddContentGalleryRecyclerView.getChildViewHolder(it) as GalleryHolder
        val imageBtn =  clickViewHolder.binding.itemGalleryViewSelectorImgBtn
        if(isSelectImgCount < 3) {
            imageBtn.isSelected = !imageBtn.isSelected
            if(!imageBtn.isSelected){
                isSelectImgCount--
                removeImage(clickViewHolder)
            }else{
                isSelectImgCount++
                uploadImageList.add(clickViewHolder.getMediaItem())
                binding.activityAddContentAddImageViewPager.visibility = View.VISIBLE
                (binding.activityAddContentAddImageViewPager.adapter as UploadImageSlidePagerAdapter).update()
            }

        }else{
            if(imageBtn.isSelected){
                imageBtn.isSelected = !imageBtn.isSelected
                isSelectImgCount--

                removeImage(clickViewHolder)
            }else {
                Toast.makeText(applicationContext, "최대 선택할 수 있는 이미지 수는  3장 입니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    fun removeImage(holder : GalleryHolder){
        uploadImageList.remove(holder.getMediaItem())
        (binding.activityAddContentAddImageViewPager.adapter as UploadImageSlidePagerAdapter).update()
        if(uploadImageList.size <= 0 ){
            binding.activityAddContentAddImageViewPager.visibility = View.GONE
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Config.FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null) {
                        val bitmap = data.extras?.get("data") as Bitmap
                        val filename = viewModel.newFileName()
                        val uri =
                            viewModel.saveImageFile(contentResolver, filename, "image/jpg", bitmap)
//                        binding.activityAddContentAddImage.setImageURI(uri)
                    }
                }
                Config.FLAG_REQ_GALLERY -> {
                    val uri = data?.data
//                    binding.activityAddContentAddImage.setImageURI(uri)
                }
            }
        }
    }


}
