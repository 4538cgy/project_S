package com.uos.smsmsm.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

class FAQRepository {
    private val TAG = "FAQRepository"
    private val db = FirebaseFirestore.getInstance()

    @ExperimentalCoroutinesApi
    fun getFAQList() = callbackFlow<Boolean> {
        val docRef = db.collection("FAQ")

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document}")
            } else {
                Log.d(TAG, "No such document")
            }
            this@callbackFlow.sendBlocking(true)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "get failed with : ",exception)
            this@callbackFlow.sendBlocking(false)
        }

        awaitClose { }
    }
}