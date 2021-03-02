package com.uos.smsmsm.data

data class ChatDTO(
    var users : MutableMap<String,Boolean> = HashMap(),
    var comments : MutableMap<String,Comment> = HashMap()
){
    data class Comment(
        var uid : String?,
        var message : String?,
        var timestamp : Long?
    )
}