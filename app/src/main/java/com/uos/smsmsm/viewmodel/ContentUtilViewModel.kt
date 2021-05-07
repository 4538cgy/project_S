package com.uos.smsmsm.viewmodel

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.FileProvider
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.repository.ContentRepository
import com.uos.smsmsm.util.GalleryUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.jvm.Throws


//게시글 업로드 , 게시글 다운로드 , 사진 업로드, 사진 다운로드 등등 Content와 관련된 모든 기능
class ContentUtilViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle,@ApplicationContext private val  appContext : Context) : ViewModel(){

    private val galleryUtil : GalleryUtil by lazy{
        GalleryUtil(appContext)
    }
    var galleryItems = MutableLiveData<MutableList<GalleryUtil.MediaItem>>()
    var contentEdittext = MutableLiveData<String>()
    val currentPhotoPath : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val contentRepository = ContentRepository()
    private val auth = FirebaseAuth.getInstance()

    var contentUploadState = MutableLiveData<String>()

    //오직 개인 한유저만의 게시글 리스트
    var userContentsList = MutableLiveData<Map<String,ContentDTO>>()

    //게시글을 업로드한 후 반환된 게시글의 id와 내용
    var uploadResultData = MutableLiveData<Map<String,ContentDTO>>()

    fun openGallery() : Intent{
        return Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }

    fun uploadComment(comments : ContentDTO.Comment,postUid : String){
        viewModelScope.launch(Dispatchers.IO){
            contentRepository.uploadComment(comments,postUid).collect {
                println("성공입니까 ? $it")
            }
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
            contentRepository.uploadContentInContents(contents,auth.currentUser?.uid.toString()).collect {
                contentUploadState.postValue("upload_content_complete")
                    //나를 구독중인 유저들의 ContentsContainer에 해당 게시글 전달하기 위해 데이터 post
                    println("게시글작성 완료 ${it.toString()}")
                    uploadResultData.postValue(it)

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

    fun requestCrop(uri : Uri) : CropImage.ActivityBuilder = CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)

    fun newFileName() : String{
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = "${sdf.format(System.currentTimeMillis())}.png"
        return filename
    }

    @Throws(IOException::class)
    public fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath.value = absolutePath
        }
    }
    fun dispatchTakePictureIntent(type : String) : Intent =
        Intent(type).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(appContext.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        appContext,
                        "com.uos.smsmsm.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
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
                    val bmOptions = BitmapFactory.Options().apply {
                        inJustDecodeBounds = false
                        inSampleSize = 1
                    }
                    BitmapFactory.decodeFile(uri.path, bmOptions)
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