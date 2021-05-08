package com.uos.smsmsm.data

data class TimeLineDTO(
    var content : ContentDTO ? = null,
    var contentId : String ? = null
){
    companion object {
        //post state
        const val POST_STATE_PUBLIC = 1         //공개
        const val POST_STATE_PRIVATE = 2        //비공개
        const val POST_STATE_ONLYFRIENDS = 3    //친구공개
        const val POST_STATE_DELETE = 4         //삭제
        const val POST_STATE_ADMOB = 5          //사진 뷰페이저를 끄고 ADMOB 을 켬 + 광고 고정 텍스트 적용
        const val POST_STATE_FACEBOOK_AUDIANCE = 6 //사진 뷰페이저를 끄고 FA 를 켬 + 광고 고정 텍스트 적용
    }
}

