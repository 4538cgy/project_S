package com.uos.smsmsm.data

data class UserDTO(
    var phoneNumber : String ? = null,
    var policyAccept : Boolean ? = null,
    var uid : String ? = null,
    var timeStamp : Long ? = null,
    var timeStr : String ? = null,
    var memberRating : Long ? = null, // 0 = 초기 가입유저 , 1 = 우수 유저 , 2 = vip 유저 , 300 = 테스트 계정 , 301 = 슈퍼계정
    var point : Long ? = null,
    var userName : String ? = null
)