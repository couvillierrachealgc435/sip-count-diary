package com.sipcount.diary.domain.repository

import com.sipcount.diary.domain.model.DayTotals
import com.sipcount.diary.domain.model.DrinkEntry
import kotlinx.coroutines.flow.StateFlow

interface DiaryRepository {
    val entries: StateFlow<List<DrinkEntry>>
    fun add(entry: DrinkEntry)
    fun remove(id: String)
    fun entriesFor(epochDay: Long): List<DrinkEntry>
    fun totalsFor(epochDay: Long): DayTotals
}
