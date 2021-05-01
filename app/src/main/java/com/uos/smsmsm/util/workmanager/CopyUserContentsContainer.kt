package com.uos.smsmsm.util.workmanager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
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
        val destinationUid = inputData.getString("WORK_DESTINATION_UID").toString()
        println("백그라운드 work 시작 1. $workState 2.$destinationUid 3. ${inputData.toString()}")
        //copy user contents collection & upload
        when(workState) {
            BackgroundWorker.WORK_COPY_PASTE_CONTENTS -> {
                println("데이터 시작!")
                mainScope.launch {
                    repository.copyUserContents(uid = destinationUid).collect {
                        println("끄아아아아아아 ${it.toString()}")
                        repository.pasteUserContentsMyContainer(it).collect{
                            println("께에에에에에에엨 ")
                        }
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

        data22.put("WORK_STATE" , BackgroundWorker.WORK_COPY_PASTE_CONTENTS)
        data22.put("WORK_UID" , FirebaseAuth.getInstance().uid.toString())

        val inputData = Data.Builder().putAll(data22).build()

        val uploadManager : WorkRequest = OneTimeWorkRequestBuilder<BackgroundWorker>().setInputData(inputData).build()
        WorkManager.getInstance(binding.root.context).enqueue(uploadManager)
     */

    companion object{
        const val WORK_COPY_PASTE_CONTENTS = 1001
        const val WORK_DELETE_CONTENTS = 1002
        const val WORK_MYSUBSCRIBE_CONTAINER_UPDATE = 1003
    }
}