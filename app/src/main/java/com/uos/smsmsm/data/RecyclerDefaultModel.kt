package com.uos.smsmsm.data

data class RecyclerDefaultModel(
    val type: Int, // type
    val text: String,  // title
    val imageUrl: Int,  // image
    val content: String?    // content
){
    companion object {
        const val TEXT_TYPE = 10        // 타이틀 한가지만 있는 경우
        const val TEXT_TYPE_2 = 11      // 타이틀과 내용이 같이 있는 경우
        const val IMAGE_TYPE = 21       // 이미지와 타이틀이 같이 있는 경우
        const val IMAGE_TYPE_2 = 22     // 이미지와 타이틀과 내용이 같이 있는 경우
    }
}