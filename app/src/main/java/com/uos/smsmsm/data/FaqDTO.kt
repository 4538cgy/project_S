package com.uos.smsmsm.data

data class FaqDTO(
    var title : String = "",
    var haschild : Boolean = false,
    var hasanswer : Boolean = false,
    var childuk : String? = null,
    var question : String? = null,
    var answer : String? = null
)