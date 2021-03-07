package com.uos.smsmsm.util.time

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeUtil {

    // Fix Deprecated
    fun getTime() = SimpleDateFormat(
        "yyyy년 MM월 dd일 a hh시 mm분 ss초",
        Locale.KOREA
    ).format(Date(System.currentTimeMillis())).toString()

    /** 몇분전, 방금 전,  */
    private object TIME_MAXIMUM {
        const val SEC = 60
        const val MIN = 60
        const val HOUR = 24
        const val DAY = 30
        const val MONTH = 12
    }

    // return-when 하나로 통합
    fun formatTimeString(regTime: Long): String {
        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - regTime) / 1000
        return when {
            diffTime < TIME_MAXIMUM.SEC -> "방금 전"
            TIME_MAXIMUM.SEC.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MIN -> {
                diffTime.toString() + "분 전"
            }
            TIME_MAXIMUM.MIN.let { diffTime /= it; diffTime } < TIME_MAXIMUM.HOUR -> {
                diffTime.toString() + "시간 전"
            }
            TIME_MAXIMUM.HOUR.let { diffTime /= it; diffTime } < TIME_MAXIMUM.DAY -> {
                diffTime.toString() + "일 전"
            }
            TIME_MAXIMUM.DAY.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MONTH -> {
                diffTime.toString() + "달 전"
            }
            else -> diffTime.toString() + "년 전"
        }
    }
}
