package com.sipcount.diary.domain.usecase

import com.sipcount.diary.domain.repository.DiaryRepository

class RemoveDrinkUseCase(private val diaryRepository: DiaryRepository) {
    operator fun invoke(entryId: String) {
        diaryRepository.remove(entryId)
    }

    fun removeLast(epochDay: Long) {
        val last = diaryRepository.entriesFor(epochDay).maxByOrNull { it.minuteOfDay }
        if (last != null) diaryRepository.remove(last.id)
    }
}
