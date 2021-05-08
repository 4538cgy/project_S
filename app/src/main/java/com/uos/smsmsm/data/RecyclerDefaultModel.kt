package com.uos.smsmsm.data

// Apply ktlint
data class RecyclerDefaultModel(
    val type: Int, // type
    val downloadImageUrl: String?,
    val uid: String?,
    val imageUrl: Int?, // image
    val title: String, // title
    val content: String? // content

) {

    // 친구 목록 모델
    data class FirendsRecyclerModel(
        val type: Int, // type
        val downloadImageUrl: String,
        val title: String, // title
        val content: String? // content
    )

    // 설정 목록 모델
    data class SettingRecyclerModel(
        val type: Int, // type
        val imageUrl: Int?, // image
        val title: String, // title
        val content: String? // content
    )

    // 채팅 리스트 목록 모델
    data class ChatListRecyclerModel(
        var users: MutableMap<String, Boolean> = HashMap(),
        var comments: MutableMap<String, Comment> = HashMap()
    ) {
        data class Comment(
            var uid: String?,
            var message: String?,
            var timestamp: Long?
        )
    }

    // 타임라인 목록 모델
    data class ContentRecyclerModel(
        var explain: String?, // 내용
        var imageDownLoadUrlList: ArrayList<String>?, // 이미지 리스트
        var uid: String?, // uid
        var timestamp: String?, // System.currentTime()
        var commentCount: Int?, // 댓글 갯수
        var favoriteCount: Int?, // 좋아요 갯수
        var favoritedUser: MutableMap<String, Boolean> = HashMap() //
    ) {
        // 댓글
        data class Comment(
            var uid: String?, // uid
            var userNickname: String?, // 유저 닉네임
            var comment: String?, // 댓글 내용
            var timestamp: Long?, // System.currentTime()
            var favoriteCount: Int?, // 좋아요 갯수
            var favoriteUser: MutableMap<String, Boolean> = HashMap() // 좋아요 누른사람 목록
        ) {
            // 대댓글
            data class AdditionCommet(
                var uid: String?, // uid
                var userNickname: String?, // 유저 닉네임
                var comment: String?, // 댓글 내용
                var timestamp: Long?, // System.currentTime()
                var favoriteCount: Int?, // 좋아요 갯수
                var favoriteUser: MutableMap<String, Boolean> = HashMap()
            )
        }
    }

    companion object {
        //view type
        const val TEXT_TYPE = 10 // 타이틀 한가지만 있는 경우
        const val TEXT_TYPE_2 = 11 // 타이틀과 내용이 같이 있는 경우
        const val IMAGE_TYPE = 21 // 이미지와 타이틀이 같이 있는 경우
        const val IMAGE_TYPE_2 = 22 // 이미지와 타이틀과 내용이 같이 있는 경우
        const val FRIENDS_LIST_TYPE_TITLE = 31 // 카톡과 같은 기본 친구 목록의 뷰
        const val FRIENDS_LIST_TYPE_TITLE_CONTENT = 32
    }


}
