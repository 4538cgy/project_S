package com.uos.smsmsm.data

import androidx.databinding.ObservableField

// Apply ktlint
data class ChatDTO(
    var chatuid : String? =null,
    var commentTimestamp : Long ? = null,
    var lastComment : String ? = null,
    var chatType : String? =null,
    var chatTitle : String? =null,
    var users: MutableMap<String, Boolean> = HashMap(),
    var comments: MutableMap<String, Comment> = HashMap()
) {
    data class Comment(
        var uid: String? = null,
        var message: String? = null,
        var timestamp: Long? = null
    )

}
