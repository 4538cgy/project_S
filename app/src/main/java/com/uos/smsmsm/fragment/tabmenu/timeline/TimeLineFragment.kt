package com.uos.smsmsm.fragment.tabmenu.timeline

import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.content.AddContentActivity
import com.uos.smsmsm.activity.content.UploadImageSlidePagerAdapter
import com.uos.smsmsm.activity.content.UploadImgDTO
import com.uos.smsmsm.databinding.FragmentTimeLineBinding
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.GalleryUtil
import com.uos.smsmsm.util.MediaType
import com.uos.smsmsm.util.isPermitted
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeLineFragment : Fragment() {

    lateinit var binding: FragmentTimeLineBinding
    private var isOpenFAB = false
    private val viewModel : ContentUtilViewModel by viewModels()

    companion object { // var -> const val
        const val PICK_PROFILE_FROM_ALBUM = 101
        const val REQUEST_IMAGE_CAPTURE = 102
        const val REQUEST_VIDEO_CAPTURE = 103
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // View? -> View (NonNull)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_line, container, false)
        binding.fragmenttimeline = this
        binding.lifecycleOwner = this
        //viewModel = ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(ContentUtilViewModel::class.java)

        return binding.root
    }

    fun uploadPhoto(view: View) {
        /*
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
         */
         */

        activity?.startActivityForResult(viewModel.openGallery(),PICK_PROFILE_FROM_ALBUM)
    }

    fun takePhotoCamera(view: View) {
        if(isPermitted(requireActivity(), Config.CAMERA_PERMISSION )) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }else{
            ActivityCompat.requestPermissions(requireActivity() , Config.CAMERA_PERMISSION,
                Config.FLAG_PERM_CAMERA)
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Config.FLAG_PERM_CAMERA) {
            if (isPermitted(requireContext(), Config.CAMERA_PERMISSION)) {
                startActivityForResult(viewModel.openCamera(), Config.FLAG_REQ_CAMERA)
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //fun takePhotoGallery(view: View) {activity?.startActivityForResult(viewModel.openGallery(),PICK_PROFILE_FROM_ALBUM)}
    fun takeVideo(view: View) {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    fun writeContent(view: View) { startActivity(Intent(binding.root.context,AddContentActivity::class.java))}

    fun clickFab(view: View) {
        isOpenFAB = if (!isOpenFAB) {
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabVideo, "translationY", -600f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabWritePost, "translationY", -400f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabCamera, "translationY", -200f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineTextviewVideo, "translationY", -600f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineTextviewWritePost, "translationY", -400f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineTextviewCamera, "translationY", -200f)
                .apply { start() }

            binding.fragmentTimeLineTextviewWritePost.visibility = View.VISIBLE
            binding.fragmentTimeLineTextviewVideo.visibility = View.VISIBLE
            binding.fragmentTimeLineTextviewCamera.visibility = View.VISIBLE
            true
        } else {
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabVideo, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabWritePost, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineFabCamera, "translationY", -0f)
                .apply { start() }

            ObjectAnimator.ofFloat(binding.fragmentTimeLineTextviewVideo, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineTextviewWritePost, "translationY", -0f)
                .apply { start() }
            ObjectAnimator.ofFloat(binding.fragmentTimeLineTextviewCamera, "translationY", -0f)
                .apply { start() }
            binding.fragmentTimeLineTextviewWritePost.visibility = View.INVISIBLE
            binding.fragmentTimeLineTextviewVideo.visibility = View.INVISIBLE
            binding.fragmentTimeLineTextviewCamera.visibility = View.INVISIBLE
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //data.extras.get("data") as Bitmap
            //imageview.setImageBitmap(imageBitmap)
            data?.extras?.get("data")?.let {
                val bitmap = it as Bitmap
                val filename = viewModel.newFileName()
                val uri =
                    viewModel.saveImageFile(requireActivity().contentResolver, filename, "image/jpg", bitmap)
                uri?.let {
                    val intent = Intent(binding.root.context,AddContentActivity::class.java)
                    intent.putExtra("uri", uri)
                    intent.putExtra("mediaType", MediaType.Picture)
                    startActivity(intent)
                } ?: {
                    Toast.makeText(context, "사진 촬영에 실패하였습니다.", Toast.LENGTH_LONG)
                        .show()
                }()

            }

        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            //val videoUri: Uri = intent.data
            //videoView.setVideoURI(videoUri)
            data?.data?.let{
                val uri = it
                val intent = Intent(binding.root.context,AddContentActivity::class.java)
                intent.putExtra("uri", uri)
                intent.putExtra("mediaType", MediaType.Video)
                startActivity(intent)
            } ?: {
                Toast.makeText(context, "영상 촬영에 실패하였습니다.", Toast.LENGTH_LONG)
                    .show()
            }()
        }
    }


}
