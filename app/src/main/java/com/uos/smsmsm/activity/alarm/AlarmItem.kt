package com.uos.smsmsm.activity.alarm

import java.util.*
//Alaram 아이템 관련
data class AlarmItem (
    val type : AlarmType,
    val textAlarmItem: TextAlarmItem?,
    val headerItem: HeaderItem?
){
    data class TextAlarmItem(
        val fristLineString : String,
        val secondLineString : String
    )
    data class HeaderItem (
        val date : Date
    )
}