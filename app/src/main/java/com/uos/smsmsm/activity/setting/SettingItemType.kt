package com.uos.smsmsm.activity.setting

/**
 * Created by SungBin on 3/14/21.
 */

sealed class SettingItemType {
    object LOGOUT : SettingItemType()
    object NOTICE : SettingItemType() // 공지사항
    object VERSION : SettingItemType() // 버전정보
    object NOTIFICATION : SettingItemType() // 알림
    object FRIEND : SettingItemType()
    object CHAT : SettingItemType()
    object CHATGROUP : SettingItemType() // 채팅 모아보기
    object OWNSTORY : SettingItemType() // 내가 올린 글 모아보기
    object THEME : SettingItemType()
    object OTHER : SettingItemType()
    object HELP : SettingItemType() // 고객센터 도움말

    object INDICATE : SettingItemType() // 구분선
}
