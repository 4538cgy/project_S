package com.uos.smsmsm.util.workmanager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.uos.smsmsm.repository.BackgroundRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BackgroundWorker(context: Context, worker : WorkerParameters) : Worker(context,worker){

    private val repository = BackgroundRepository()
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun doWork(): Result {

        val workState = inputData.getInt("WORK_STATE",1)
        val uid = inputData.getString("WORK_UID").toString()

        //copy user contents collection & upload
        when(workState) {
            BackgroundWorker.WORK_COPY_PASTE_CONTENTS -> {
                mainScope.launch {
                    repository.copyUserContents(uid = uid).collect {

                    }
                }
            }
            BackgroundWorker.WORK_MYSUBSCRIBE_CONTAINER_UPDATE ->{

            }
            BackgroundWorker.WORK_DELETE_CONTENTS ->{

            }
        }

        return Result.success()
    }



    //how use this
    /*
        var data22 : MutableMap<String,Any> = HashMap()

        data22.put("work_state" , BackgroundWorker.WORK_COPY_PASTE_CONTENTS.toString())
        data22.put("work_uid" , FirebaseAuth.getInstance().uid.toString())

        val inputData = Data.Builder().putAll(data22)

        val uploadManager : WorkRequest = OneTimeWorkRequestBuilder<BackgroundWorker>().setInputData(inputData).build()
        WorkManager.getInstance(binding.root.context).enqueue(uploadManager)
     */

    companion object{
        const val WORK_COPY_PASTE_CONTENTS = 1001
        const val WORK_DELETE_CONTENTS = 1002
        const val WORK_MYSUBSCRIBE_CONTAINER_UPDATE = 1003
    }
}