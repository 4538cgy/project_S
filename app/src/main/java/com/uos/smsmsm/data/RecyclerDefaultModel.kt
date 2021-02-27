package com.uos.smsmsm.data

data class RecyclerDefaultModel(
    val type: Int, // type
    val downloadImageUrl : String,
    val imageUrl: Int?,  // image
    val title: String, //title
    val content: String?    // content


){
    companion object {
        const val TEXT_TYPE = 10        // 타이틀 한가지만 있는 경우
        const val TEXT_TYPE_2 = 11      // 타이틀과 내용이 같이 있는 경우
        const val IMAGE_TYPE = 21       // 이미지와 타이틀이 같이 있는 경우
        const val IMAGE_TYPE_2 = 22     // 이미지와 타이틀과 내용이 같이 있는 경우
        const val FRIENDS_LIST_TYPE_TITLE = 31 // 카톡과 같은 기본 친구 목록의 뷰
        const val FRIENDS_LIST_TYPE_TITLE_CONTENT = 32
    }
    

}