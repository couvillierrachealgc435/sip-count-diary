package com.sipcount.diary.domain.model

data class DrinkEntry(
    val id: String,
    val epochDay: Long,
    val minuteOfDay: Int,
    val vesselId: String,
    val typeId: String,
    val volumeMl: Int
)
