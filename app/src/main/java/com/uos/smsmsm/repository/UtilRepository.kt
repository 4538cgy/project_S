package com.uos.smsmsm.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.uos.smsmsm.data.ReportDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class UtilRepository {

    private val db = FirebaseFirestore.getInstance()

    @ExperimentalCoroutinesApi
    fun saveUserReport(reportData : ReportDTO.USER) = callbackFlow<Boolean> {
        val databaseReference = db.collection("UserReport").document()
        val eventListener = databaseReference.set(reportData)

        eventListener.addOnCompleteListener { this@callbackFlow.sendBlocking(true) }.addOnFailureListener { this@callbackFlow.sendBlocking(false) }

        awaitClose { eventListener }
    }

}