package com.uos.smsmsm.activity.content

import com.uos.smsmsm.util.GalleryUtil

data class UploadImgDTO(
    var galleryHolder : GalleryHolder?,
    var mediaItem: GalleryUtil.MediaItem?
)