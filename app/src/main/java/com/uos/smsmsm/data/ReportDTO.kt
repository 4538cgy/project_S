package com.uos.smsmsm.data

class ReportDTO{
    data class POST(
        var postExplain : String?,              //신고당한 게시글의 내용
        var reportUserUid : String?,            //신고한 유저의 uid
        var destinationUid : String?,           //신고당한 유저의 uid
        var postUid : String?,                  //게시글의 snapshot.id
        var timestamp : Int?,                   //신고 당한 시간
        var reportExplain: String               //신고 당한 사유
    )
    
    data class USER(
        var reportUserUid : String?,            //신고한 유저의 uid
        var destinationUid : String?,           //신고당한 유저의 uid
        var timestamp : Int?,                   //신고 당한 시간
        var reportExplain : String              //신고 당한 사유
    )
}