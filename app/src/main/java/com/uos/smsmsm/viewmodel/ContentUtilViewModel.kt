package com.uos.smsmsm.viewmodel

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.RecyclerDefaultModel
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.util.GalleryUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URI
import java.text.SimpleDateFormat
import javax.inject.Inject


//게시글 업로드 , 게시글 다운로드 , 사진 업로드, 사진 다운로드 등등 Content와 관련된 모든 기능
class ContentUtilViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,@ApplicationContext private val  appContext : Context) : ViewModel(){

    private val galleryUtil : GalleryUtil by lazy{
        GalleryUtil(appContext)
    }
    var galleryItems = MutableLiveData<MutableList<GalleryUtil.MediaItem>>()
    var contentEdittext = MutableLiveData<String>()
    private val contentRepository = ContentRepository()
    private val auth = FirebaseAuth.getInstance()

    var contentUploadState = MutableLiveData<String>()

    //오직 개인 한유저만의 게시글 리스트
    var userContentsList = MutableLiveData<Map<String,ContentDTO>>()

    fun openGallery() : Intent{
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    fun uploadPhoto(contents : ContentDTO , photoList : ArrayList<Uri>){

        var photoDownLoadUrlList = arrayListOf<String>()

        contentUploadState.postValue("upload_photo")
        photoList.forEach {
            viewModelScope.launch(Dispatchers.IO){
                contentRepository.uploadPhoto(it).collect {
                    photoDownLoadUrlList.add(it)
                    if (photoList.size == photoDownLoadUrlList.size) {
                        contentUploadState.postValue("upload_photo_complete")
                        uploadContent(contents,photoDownLoadUrlList)
                    }
                }
            }
        }
    }

    fun uploadContent(contents : ContentDTO , photoDownLoadUrl : ArrayList<String> ? = null){

        contents.imageDownLoadUrlList = photoDownLoadUrl
        contentUploadState.postValue("upload_content")
        viewModelScope.launch(Dispatchers.IO){
            contentRepository.uploadContent(contents,auth.currentUser?.uid.toString()).collect {
                contentUploadState.postValue("upload_content_complete")
                if (it != "false") {
                    print("업로드 성공")
                    //나를 구독중인 유저들의 ContentsContainer에 해당 게시글 전달

                }else println("업로드 실패라능")
            }
        }
    }

    fun getUserTimeLinePosts(uid : String) {
        viewModelScope.launch(Dispatchers.IO){
            contentRepository.getUserPostContent(uid).collect {
                userContentsList.postValue(it)
            }
        }
    }

    fun openCamera() : Intent{
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    fun newFileName() : String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return filename;
    }
    //이미지 저장
    fun saveImageFile(contentResolver: ContentResolver, filename:String, mimeType:String, bitmap: Bitmap) : Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        //안드로이드 버전이 Q보다 크거나 같으면
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING, 1)   //사용중임을 알려주는 코드
        }

        //내가 저장할 사진의 주소값 생성
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try{
            if(uri != null){
                //쓰기모드 열기
                var descriptor = contentResolver.openFileDescriptor(uri,"w")
                if(descriptor != null){
                    val fos = FileOutputStream(descriptor.fileDescriptor)   //OutputStream 예외처리
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()
                    return uri
                }
            }
        }catch (e: Exception){
            Log.e("Camera","${e.localizedMessage}")
        }

        return null
    }
    fun getGallery() {
        viewModelScope.launch {
            galleryItems.postValue( galleryUtil.getGerryItem())
        }
    }

    //에딧 텍스트 TextWatcher
    fun textWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            contentEdittext.postValue(s.toString())
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            contentEdittext.postValue(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            contentEdittext.postValue(s.toString())
        }

    }
}