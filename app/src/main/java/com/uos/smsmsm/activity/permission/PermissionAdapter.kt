package com.uos.smsmsm.activity.permission

import android.Manifest
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uos.smsmsm.R
import com.uos.smsmsm.databinding.LayoutPermissionBinding
import kotlin.random.Random

/**
 * Created by SungBin on 3/28/21.
 */

class PermissionAdapter(private val permissions: List<Permission>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(
        private val binding: LayoutPermissionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(permission: Permission) {
            binding.item = permission
            binding.ivIcon.setImageResource(
                when (permission.type) {
                    0 -> R.drawable.ic_perm_contact_calendar_black_24dp
                    1 -> R.drawable.ic_camera_alt_black_24dp
                    2 -> R.drawable.ic_contact_phone_black_24dp
                    3 -> R.drawable.ic_location_on_black_24dp
                    4 -> R.drawable.ic_mic_black_24dp
                    5 -> R.drawable.ic_local_phone_black_24dp
                    6 -> R.drawable.ic_memory_black_24dp
                    7 -> R.drawable.ic_sms_black_24dp
                    else -> R.drawable.ic_folder_black_24dp
                }
            )
        }
    }

    fun requestPermissions(activity: Activity) {
        val requestPermissionsString = mutableListOf<String>().apply {
            // add(permissions.forEach(::getPermissionList)) todo: 이거 되게 못하나
            permissions.forEach { permission ->
                addAll(getPermissionList(permission))
            }
        }
        ActivityCompat.requestPermissions(activity, requestPermissionsString.toTypedArray(), 1000)
    }

    private fun getPermissionList(permission: Permission): List<String> {
        return when (permission.type) {
            0 -> listOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            )
            1 -> listOf(Manifest.permission.CAMERA)
            2 -> listOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS
            )
            3 -> listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            4 -> listOf(Manifest.permission.RECORD_AUDIO)
            5 -> listOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE, Manifest.permission.USE_SIP
            )
            6 -> listOf(Manifest.permission.BODY_SENSORS)
            7 -> listOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS
            )
            else -> listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_permission,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(permissions[position])
    }

    override fun getItemCount() = permissions.size
    override fun getItemId(position: Int) = position.toLong()
    override fun getItemViewType(position: Int) = position
}
