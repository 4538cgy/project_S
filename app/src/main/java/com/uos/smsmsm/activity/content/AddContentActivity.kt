package com.uos.smsmsm.activity.content

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.R
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.databinding.ActivityAddContentBinding
import com.uos.smsmsm.ui.bottomsheet.BottomSheetDialogWriteContent
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.GalleryUtil.MediaItem
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContentActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddContentBinding
    private val viewModel: ContentUtilViewModel by viewModels()
    private var isSelectImgCount: Int = 0
    private val MAX_SELECT_COUNT = 5
    private var uploadImageList = ArrayList<UploadImgDTO>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_content)
        binding.apply {
            lifecycleOwner = this@AddContentActivity
            activityaddcontent = this@AddContentActivity
            viewmodel = viewModel
        }

        //업로드 버튼 비활성화
        binding.activityAddContentButton.isEnabled = false

        binding.activityAddContentGalleryRecyclerView.layoutManager =
            GridLayoutManager(applicationContext, 3)
        binding.activityAddContentAddImageViewPager.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = UploadImageSlidePagerAdapter(
                uploadImageList,
                { it ->
                    val viewHolder =
                        binding.activityAddContentAddImageViewPager.getChildViewHolder(it.parent as View) as UploadImageViewHolder
                    viewHolder.holder?.let {
                        isSelectImgCount--
                        removeImage(it, null)
                        val imageBtn = it.binding.itemGalleryViewSelectorImgBtn
                        imageBtn.isSelected = false
                    }
                    viewHolder.mediaItem?.let {
                        removeImage(null, it)
                    }
                }, applicationContext
            )
        }
        binding.activityAddContentButton.setOnClickListener {
            // 이미지 업로드 버튼 동작
            // 업로드할 이미지 정보들은 uploadImageList 를 활용하면 된다.
            // uploadImgDTO에 GalleryHolder 와 MediaItem 2 종류가 있는데
            // 하단 갤러리에서 선택할 경우 GalleryHolder로 등록이 촬영일 경우 MediaItem으로 등록 된다.

        }

        viewModel.contentEdittext.observe(this, Observer {
            interactiveView()
        })
    }

    //게시글 올리기
    fun uploadPost(view:View){

        println("게시글 올리기")

        var contents = ContentDTO()
        contents.explain = viewModel.contentEdittext.value.toString()
        contents.timestamp = System.currentTimeMillis()
        contents.uid = auth.uid.toString()
        contents.postState = "public"

        var photoImageList = arrayListOf<Uri>()

        println(viewModel.galleryItems.value.toString())

        //이미지를 uri로 변환

        if (viewModel.galleryItems.value != null)
        viewModel.galleryItems.value!!.forEach {
            photoImageList.add(it.contentUri)
        }



        if (photoImageList.isEmpty()){
            println("선택한 사진이 존재하지 않으므로 photo가 없는 data만 업로드 진행")
            viewModel.uploadContent(contents,null)
        }else{
            println("선택한 사진이 존재하므로 photo upload 진행")
            println("업로드 하는 사진의 size = ${photoImageList.size}")
            println("업로드 하는 사진 = ${photoImageList.toString()}")
           // viewModel.uploadPhoto(contents,photoImageList)
        }
    }

    //게시글 옵션 선택 바텀 시트 열기
    fun openContentOptionSelector(view: View){
        val bottomSheetDialog = BottomSheetDialogWriteContent()
        bottomSheetDialog.show(supportFragmentManager, "contentOption")
    }

    //업로드 버튼 활성화
    fun interactiveView(){
        if (viewModel.contentEdittext.value != null && viewModel.contentEdittext.value!!.isNotEmpty()){
            binding.activityAddContentButton.isEnabled = true
            binding.activityAddContentButton.setTextColor(Color.BLACK)
        }else{
            binding.activityAddContentButton.isEnabled = false
            binding.activityAddContentButton.setTextColor(Color.LTGRAY)
        }
    }
    //하단 갤러리 오픈
    fun openGallery(view: View) {
        binding.activityAddContentGalleryRecyclerView.run {
            val gallery = binding.activityAddContentGallery
            if (!viewModel.galleryItems.hasObservers()) {
                viewModel.galleryItems.observe(
                    this@AddContentActivity,
                    androidx.lifecycle.Observer { it ->
                        Log.d("Test", "size : ${it.size}")
                        // 갤러리가 닫혀 있으면 오픈하면서 데이터 적용
                        if (gallery.visibility == View.GONE) {
                            adapter = GalleryAdapter(it, applicationContext, clickListener)
                        } else {
                            // 열려 있다면 리스트 수정하여 업데이트
                            (adapter as GalleryAdapter).galleryLists = it
                            (adapter as GalleryAdapter).notifyDataSetChanged()
                        }

                        gallery.visibility = View.VISIBLE

                    })
            }
            // 갤러리가 닫혀 있으면 이미지 데이터 요청
            if (gallery.visibility == View.GONE) {
                viewModel.getGallery()
            }
        }

    }
    // 사진 촬영
    fun takePicture(view: View) {
        binding.activityAddContentGallery.visibility = View.GONE
        startActivityForResult(viewModel.openCamera(), Config.FLAG_REQ_CAMERA)
    }
    // 하단 갤러리에서 사진 선택 시 동작
    private val clickListener = View.OnClickListener {

        val clickViewHolder =
            binding.activityAddContentGalleryRecyclerView.getChildViewHolder(it) as GalleryHolder
        val imageBtn = clickViewHolder.binding.itemGalleryViewSelectorImgBtn
        // 최대 선택 갯수 현재 3개
        if (isSelectImgCount < MAX_SELECT_COUNT) {
            imageBtn.isSelected = !imageBtn.isSelected
            // 이미 선택 되었다면 선택 해제
            if (!imageBtn.isSelected) {
                isSelectImgCount--
                removeImage(clickViewHolder, null)
            } else {
                // 새로 선택
                isSelectImgCount++
                uploadImageList.add(UploadImgDTO(clickViewHolder, null))
                binding.activityAddContentAddImageViewPager.run {
                    visibility = View.VISIBLE
                    (adapter as UploadImageSlidePagerAdapter).notifyDataSetChanged()
                }
            }
        } else {
            // 3개 이상 선택 되었을 때
            // 이미 선택되 항목을 선택할 경우
           if (imageBtn.isSelected) {
                imageBtn.isSelected = !imageBtn.isSelected
                isSelectImgCount--
                removeImage(clickViewHolder, null)
            } else {
               // 신규 선택 할 경우
                Toast.makeText(applicationContext, "최대 선택할 수 있는 이미지 수는  ${MAX_SELECT_COUNT}장 입니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    // 하단 갤러리나 추가할 이미지 프리뷰에서 클로즈 버튼을 통하여 업로드할 이미지 제거할 경우
    private fun removeImage(holder: GalleryHolder?, mediaItem: MediaItem?) {

        for (it in uploadImageList) {
            if (it.galleryHolder != null && it.galleryHolder == holder) {
                uploadImageList.remove(it)
                break
            } else if (it.mediaItem != null && it.mediaItem == mediaItem) {
                uploadImageList.remove(it)
                break
            }
        }
        binding.activityAddContentAddImageViewPager.run {
            (adapter as UploadImageSlidePagerAdapter).notifyDataSetChanged()
            if (uploadImageList.size <= 0) {
                visibility = View.GONE
            }
        }
    }
    // 사진 촬영 후 그 결과 처리
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
                        uri?.let {
                            uploadImageList.add(UploadImgDTO(null, MediaItem(null, null, null, it)))
                            binding.activityAddContentAddImageViewPager.run {
                                visibility = View.VISIBLE
                                (adapter as UploadImageSlidePagerAdapter).notifyDataSetChanged()
                            }
                        } ?: {
                            Toast.makeText(applicationContext, "사진 촬영에 실패하였습니다.", Toast.LENGTH_LONG)
                                .show()
                        }()
                    }
                }
            }
        }
    }


}
