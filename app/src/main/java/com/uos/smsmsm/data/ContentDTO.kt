package com.uos.smsmsm.data

data class ContentDTO(
    //작성자 uid
    var uid : String ? =null,
    //작성된 시간
    var timestamp : Long ? = null,
    //이미지들 리스트
    var imageDownLoadUrlList : ArrayList<String> ? = null,
    //게시글 상태 - private [비공개] , public [공개] delete [삭제] friend [ 친구공개 ]
    var postState : String ? = null,
    //게시글 내용
    var explain : String ? = null,
    //댓글 갯수
    var commentCount : Int ? = 0,
    //좋아요 갯수
    var favoriteCount : Int ? = 0,
    //노출수
    var viewCount : Int ? = 0,
    //좋아요 누른 사람
    var favorites : MutableMap<String,Boolean> = HashMap(),
    //노출된 사람 uid
    var viewers : MutableMap<String,Boolean> = HashMap(),
    //조회수
    var viewActionCount : Int ? = 0,
    //조회한 사람 uid
    var viewActioners : MutableMap<String,Boolean> = HashMap()
) {
    data class Comment(
        var uid : String ? = null,
        var userId : String ? = null,
        var comment : String ? = null,
        var timestamp : Long ? = null
    ){
        data class ReplyComment(
            var masterCommentUid : String ? = null,
            var userId : String ? = null,
            var uid : String ? = null,
            var comment : String ? = null,
            var timestamp: Long? = null
        )
    }
}