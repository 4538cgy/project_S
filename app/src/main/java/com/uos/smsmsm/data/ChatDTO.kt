package com.uos.smsmsm.data

// Apply ktlint
data class ChatDTO(
    var viewType: Int?,
    var users: MutableMap<String, Boolean> = HashMap(),
    var comments: MutableMap<String, Comment> = HashMap()
) {
    data class Comment(
        var uid: String?,
        var message: String?,
        var timestamp: Long?
    )

    companion object {
        const val ONE_TO_ONE = 1 // 1:1대화 리스트
        const val GROUP = 2 // 그룹채팅 대화 리스트
    }
}
