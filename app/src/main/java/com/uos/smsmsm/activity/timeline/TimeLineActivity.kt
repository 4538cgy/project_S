package com.uos.smsmsm.activity.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.uos.smsmsm.R
import com.uos.smsmsm.data.ContentDTO
import com.uos.smsmsm.data.TimeLineDTO
import com.uos.smsmsm.databinding.ActivityTimeLineBinding
import com.uos.smsmsm.recycleradapter.timeline.TimeLineRecyclerAdapter
import com.uos.smsmsm.viewmodel.ContentUtilViewModel
import com.uos.smsmsm.viewmodel.UserUtilViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.Observer
import com.uos.smsmsm.base.BaseActivity

@AndroidEntryPoint
class TimeLineActivity : BaseActivity<ActivityTimeLineBinding>(R.layout.activity_time_line) {

    private val viewModel: ContentUtilViewModel by viewModels()
    private val userViewModel : UserUtilViewModel by viewModels()
    private var destinationUid : String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewmodel = viewModel
            activitytimeline = this@TimeLineActivity
            userviewmodel = userViewModel
        }
        destinationUid = intent.getStringExtra("destinationUid")
        //유저 닉네임 가져오기
        userViewModel.getUserName(destinationUid.toString())
        userViewModel.userName.observe(this, androidx.lifecycle.Observer {
            binding.activityTimeLineTextviewTitle.text = it.toString() + "의 타임라인"
        })

        //유저의 contents가져오기
        viewModel.getUserTimeLinePosts(destinationUid.toString())

        initRecyclerViewAdapter()

    }

    fun initRecyclerViewAdapter(){
        val data = MutableLiveData<ArrayList<TimeLineDTO>>()
        val timelineData = arrayListOf<TimeLineDTO>()
        val recyclerObserver : Observer<Map<String,ContentDTO>>
                = Observer { livedata ->

            livedata.forEach {
                timelineData.add(TimeLineDTO(it.value,it.key))
            }
            data.value = timelineData

            //데이터 변동되면 리사이클러뷰에 넣기
            binding.activityTimeLineRecycler.adapter  =  TimeLineRecyclerAdapter(binding.root.context,data)
            binding.activityTimeLineRecycler.layoutManager = LinearLayoutManager(binding.root.context,LinearLayoutManager.VERTICAL,false)

        }
        viewModel.userContentsList.observe(this, recyclerObserver)
    }

    fun onBack(view: View){ finish() }

    fun selectSubject(view : View){
        when(view.id){
            R.id.activity_time_line_textview_contents ->{
            }
            R.id.activity_time_line_textview_media ->{
            }
        }
    }
}