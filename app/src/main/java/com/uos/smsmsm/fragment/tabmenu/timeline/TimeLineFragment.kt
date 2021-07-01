package com.uos.smsmsm.fragment.tabmenu.timeline

import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theartofdev.edmodo.cropper.CropImage
import com.uos.smsmsm.R
import com.uos.smsmsm.activity.content.AddContentActivity
import com.uos.smsmsm.base.BaseFragment
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.FragmentTimeLineBinding
import com.uos.smsmsm.recycleradapter.timeline.TimeLineAdapter
import com.uos.smsmsm.recycleradapter.timeline.TimeLineRecyclerAdapter
import com.uos.smsmsm.util.Config
import com.uos.smsmsm.util.MediaType
import com.uos.smsmsm.util.isPermitted
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import com.uos.smsmsm.viewmodel.SNSUtilViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class TimeLineFragment : BaseFragment<FragmentTimeLineBinding>(R.layout.fragment_time_line) {

    lateinit var currentPhotoPath: String
    private var isOpenFAB = false

    private var data = MutableLiveData<ArrayList<TimeLineDTO>>()

    private var count = 0

    private val adapter by lazy { TimeLineAdapter(requireActivity().supportFragmentManager).apply {
        submitList(list)
    } }

    private val list by lazy { ArrayList<TimeLineDTO>().apply {
    }}

    override fun init() {
        snsViewModel.getTimeLineData()
        super.init()
        initRecyclerView()
    }

    private fun initRecyclerView(){
        binding.fragmentTimeLineRecycler.adapter = adapter
        binding.fragmentTimeLineRecycler.addOnScrollListener(object  : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.fragmentTimeLineRecycler.canScrollVertically(1))
                {
                    println("끝에 도달")
                    snsViewModel.getData()
                }
            }
        })
        binding.fragmentTimeLineRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)
        binding.fragmentTimeLineRecycler.setHasFixedSize(true)
    }

    companion object { // var -> const val
        const val PICK_PROFILE_FROM_ALBUM = 101
        const val REQUEST_IMAGE_CAPTURE = 102
        const val REQUEST_VIDEO_CAPTURE = 103
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("ㅇㅇㅇㅇ")

        binding.fragmenttimeline = this
        binding.lifecycleOwner = this


        snsViewModel.timelineDataList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                println("옵저빙 중 ${it.toString()}")
                it.forEach {
                    list.add(TimeLineDTO(it.value, it.key))
                }
                println("옵저빙 결과 ${list.toString()}")
                adapter.submitList(list)
                //최초 item insert
                    adapter.notifyItemInserted(list.lastIndex)

            }
        })



        contentViewModel.currentPhotoPath.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            this.currentPhotoPath = it
        })
    }

    override fun onDetach() {
        super.onDetach()
        snsViewModel.timeLineDataListClear()
        snsViewModel.pagingcount = 0
    }

    fun uploadPhoto(view: View) {


        activity?.startActivityForResult(contentViewModel.openGallery(), PICK_PROFILE_FROM_ALBUM)
    }

    fun takePhotoCamera(view: View) {
        if (isPermitted(requireActivity(), Config.CAMERA_PERMISSION)) {
            startActivityForResult(contentViewModel.dispatchTakePictureIntent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), Config.CAMERA_PERMISSION,
                Config.FLAG_PERM_CAMERA
            )
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
                startActivityForResult(contentViewModel.dispatchTakePictureIntent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_IMAGE_CAPTURE)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    //fun takePhotoGallery(view: View) {activity?.startActivityForResult(viewModel.openGallery(),PICK_PROFILE_FROM_ALBUM)}
    fun takeVideo(view: View) {
        startActivityForResult(contentViewModel.dispatchTakePictureIntent(MediaStore.ACTION_VIDEO_CAPTURE), REQUEST_VIDEO_CAPTURE)
    }

    fun writeContent(view: View) {
        startActivity(Intent(binding.root.context, AddContentActivity::class.java))
    }

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
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQUEST_IMAGE_CAPTURE -> {
                    val f = File(currentPhotoPath)
                    val uri = Uri.fromFile(f)
                    uri?.let { itUri ->
                        activity?.let { it1 -> contentViewModel.requestCrop(itUri).start(it1) }
                    } ?: {
                        Toast.makeText(context, "사진 촬영에 실패하였습니다.", Toast.LENGTH_LONG).show()
                    }()
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    startActivity(
                        Intent(binding.root.context, AddContentActivity::class.java).apply {
                            putExtra("uri", result.uri)
                            putExtra("mediaType", MediaType.Picture)
                        })
                }
                REQUEST_VIDEO_CAPTURE -> {
                    val f = File(currentPhotoPath)
                    val uri = Uri.fromFile(f)
                    uri?.let { itUri ->
                        startActivity(Intent(binding.root.context, AddContentActivity::class.java).apply {
                            putExtra("uri", itUri)
                            putExtra("mediaType", MediaType.Video)
                        })
                    } ?: {
                        Toast.makeText(context, "영상 촬영에 실패하였습니다.", Toast.LENGTH_LONG).show()
                    }()
                }
            }
        }
    }


}
