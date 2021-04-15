package com.uos.smsmsm.activity.permission

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.uos.smsmsm.R
import com.uos.smsmsm.util.extensions.toast
import kotlinx.android.synthetic.main.activity_permission.*

/**
 * Created by SungBin on 3/28/21.
 */

class PermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val adapter = PermissionAdapter(
            listOf(
                Permission(PermissionType.CAMERA, "카메라", "나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈"),
                Permission(PermissionType.MICROPHONE, "카메라", "나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈고"),
                Permission(PermissionType.SMS, "카메라", "싶나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈어요"),
                Permission(PermissionType.STORAGE, "카메라", "어두운나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈나는야 성빈 나는야 성빈나는야 성빈나는야 성빈미래")
            )
        )
        rv_permissions.adapter = adapter

        btn_reject_use_permission.setOnClickListener {
            toast("권한 사용애 동의하셔야 이 앱 이용 가능합니다")
        }

        btn_agree_use_permission.setOnClickListener {
            adapter.requestPermissions(this)
        }
    }
}
