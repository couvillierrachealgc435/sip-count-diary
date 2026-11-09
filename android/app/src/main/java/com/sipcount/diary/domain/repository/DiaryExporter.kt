package com.sipcount.diary.domain.repository

interface DiaryExporter {
    fun export(label: String, text: String)
}
