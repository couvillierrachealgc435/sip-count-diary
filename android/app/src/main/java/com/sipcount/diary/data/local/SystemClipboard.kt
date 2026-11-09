package com.sipcount.diary.data.local

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.sipcount.diary.domain.repository.DiaryExporter

class SystemClipboard(context: Context) : DiaryExporter {

    private val appContext = context.applicationContext

    override fun export(label: String, text: String) {
        val manager = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        manager?.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}
