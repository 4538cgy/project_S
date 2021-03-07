package com.uos.smsmsm.data

// Apply ktlint
data class FollowDTO(
    var followerCount: Int = 0,
    var followrs: MutableMap<String, Boolean> = HashMap(),

    var followingCount: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap()
)
