package com.sipcount.diary.data.repository

import com.sipcount.diary.data.local.SipPreferences
import com.sipcount.diary.domain.model.DayTotals
import com.sipcount.diary.domain.model.DrinkEntry
import com.sipcount.diary.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiaryRepositoryImpl(private val storage: SipPreferences) : DiaryRepository {

    private val state = MutableStateFlow(storage.readEntries())

    override val entries: StateFlow<List<DrinkEntry>> = state.asStateFlow()

    override fun add(entry: DrinkEntry) {
        commit(state.value + entry)
    }

    override fun remove(id: String) {
        commit(state.value.filterNot { it.id == id })
    }

    override fun entriesFor(epochDay: Long): List<DrinkEntry> =
        state.value.filter { it.epochDay == epochDay }.sortedBy { it.minuteOfDay }

    override fun totalsFor(epochDay: Long): DayTotals {
        val dayEntries = state.value.filter { it.epochDay == epochDay }
        val perType = LinkedHashMap<String, Int>()
        dayEntries.forEach { entry ->
            perType[entry.typeId] = (perType[entry.typeId] ?: 0) + entry.volumeMl
        }
        return DayTotals(
            epochDay = epochDay,
            totalMl = dayEntries.sumOf { it.volumeMl },
            perType = perType,
            count = dayEntries.size
        )
    }

    fun replaceAll(entries: List<DrinkEntry>) {
        commit(entries)
    }

    private fun commit(entries: List<DrinkEntry>) {
        storage.writeEntries(entries)
        state.value = entries
    }
}
