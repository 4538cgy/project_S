package com.uos.smsmsm.util.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadWorker(context: Context , worker : WorkerParameters) : Worker(context,worker){

    override fun doWork(): Result {
        //uploadContent for subscribe db container
        return Result.success()
    }

    //how use this
    /*
        val uploadWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().build()

        WorkManaer.getInstance(context).euqueue(uploadWorkRequest)
     */
}