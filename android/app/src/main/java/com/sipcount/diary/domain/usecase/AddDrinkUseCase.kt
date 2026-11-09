package com.sipcount.diary.domain.usecase

import com.sipcount.diary.domain.model.DrinkEntry
import com.sipcount.diary.domain.model.Vessel
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.PreferencesRepository

data class AddDrinkResult(
    val entry: DrinkEntry,
    val totalMl: Int,
    val crossedTarget: Boolean
)

class AddDrinkUseCase(
    private val diaryRepository: DiaryRepository,
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(vessel: Vessel, epochDay: Long, minuteOfDay: Int): AddDrinkResult {
        val target = preferencesRepository.preferences.value.targetMl
        val before = diaryRepository.totalsFor(epochDay).totalMl
        val entry = DrinkEntry(
            id = "e" + epochDay + "_" + minuteOfDay + "_" + before + "_" + vessel.id,
            epochDay = epochDay,
            minuteOfDay = minuteOfDay,
            vesselId = vessel.id,
            typeId = vessel.typeId,
            volumeMl = vessel.volumeMl
        )
        diaryRepository.add(entry)
        val after = before + vessel.volumeMl
        return AddDrinkResult(
            entry = entry,
            totalMl = after,
            crossedTarget = before < target && after >= target
        )
    }
}
