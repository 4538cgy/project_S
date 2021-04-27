package com.uos.smsmsm.activity.content

import com.uos.smsmsm.util.GalleryUtil

// 갤러이에서 사진을 가져왔을 경우에는 galleryHoler가 not null이고
// 사진을 찍어서 이미지를 가져왔을 경우 mediaItem이 not null이다.
data class UploadImgDTO(
    var galleryHolder : GalleryHolder?,
    var mediaItem: GalleryUtil.MediaItem?
)