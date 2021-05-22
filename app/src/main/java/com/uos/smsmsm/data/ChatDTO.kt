package com.uos.smsmsm.data

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField

// Apply ktlint
data class ChatDTO(
    var chatuid : String? =null,
    var commentTimestamp : ObservableField<Long> ? = null,
    var chatType : String? =null,
    var chatTitle : String? =null,
    var users: ArrayList<String> = ArrayList(),
    var comments: ObservableArrayList<Comment> = ObservableArrayList()
) {
    data class Comment(
        var uid: String? = null,
        var message: String? = null,
        var timestamp: Long? = null
    )
}
