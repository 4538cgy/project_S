package com.uos.smsmsm.util

import android.Manifest

class Config(){
    companion object {
        const val FLAG_PERM_CAMERA = 98
        const val FLAG_PERM_STORAGE = 99
        const val FLAG_REQ_CAMERA = 101
        const val FLAG_REQ_GALLERY = 102
        const val FLAG_WELCOME_REQUEST_PERMISSION = 103
        val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA) //카메라 퍼미션
        val STORAGE_PERMISSION = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) //외부저장소 권한요청
        val WELCOME_REQUEST_PERMISSION  = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
    }
}
