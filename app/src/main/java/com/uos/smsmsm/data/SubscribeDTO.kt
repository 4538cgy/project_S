package com.uos.smsmsm.data

data class SubscribeDTO(

    //내가 팔로우한 사람
    var subscribingCount : Long ? = null,
    var subscribingList : MutableMap<String,SubscribingDTO> = HashMap(),
    //나를 팔로우한 사람
    var subscriberCount : Long ? = null,
    var subscriberList : MutableMap<String,SubScriberDTO> = HashMap()

){
    data class SubscribingDTO(
        var uid : String ? = null,
        var timestamp : Long ? = null
    )
    data class SubScriberDTO(
        var uid : String ? = null,
        var timestamp : Long ? = null
    )
}