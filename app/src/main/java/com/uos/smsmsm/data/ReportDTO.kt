package com.uos.smsmsm.data

// Apply ktlint
class ReportDTO {
    data class POST(
        var postExplain: String?, // 신고당한 게시글의 내용
        var reportUserUid: String?, // 신고한 유저의 uid
        var destinationUid: String?, // 신고당한 유저의 uid
        var postUid: String?, // 게시글의 snapshot.id
        var timestamp: Int?, // 신고 당한 시간
        var reportExplain: String // 신고 당한 사유
    )

    data class USER(
        var reportUserUid: String ? = null, // 신고한 유저의 uid
        var destinationUid: String ? = null , // 신고당한 유저의 uid
        var timestamp: Long ? = null, // 신고 당한 시간
        var etcCause : String ? = null, // 신고 당했는데 기타로 신고당한 경우의 사유
        var reportExplain: String ? = null // 신고 당한 사유
    )
}
