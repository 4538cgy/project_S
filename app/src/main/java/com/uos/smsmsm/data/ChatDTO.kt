package com.uos.smsmsm.data

// Apply ktlint
data class ChatDTO(
    var commentTimestamp : Long ? = null,
    var users: MutableMap<String, Boolean> = HashMap(),
    var comments: MutableMap<String, Comment> = HashMap()
) {
    data class Comment(
        var uid: String? = null,
        var message: String? = null,
        var timestamp: Long? = null
    )

}
