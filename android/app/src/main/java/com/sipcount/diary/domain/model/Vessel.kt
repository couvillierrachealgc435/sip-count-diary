package com.sipcount.diary.domain.model

data class Vessel(
    val id: String,
    val name: String,
    val volumeMl: Int,
    val spriteIndex: Int,
    val typeId: String
)
