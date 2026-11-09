package com.sipcount.diary.domain.model

data class Preferences(
    val targetMl: Int,
    val unit: VolumeUnit,
    val darkTheme: Boolean
)
