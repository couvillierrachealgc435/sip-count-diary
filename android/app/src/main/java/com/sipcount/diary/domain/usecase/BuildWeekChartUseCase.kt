package com.sipcount.diary.domain.usecase

import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.DayTotals
import com.sipcount.diary.domain.repository.DiaryRepository

class BuildWeekChartUseCase(private val diaryRepository: DiaryRepository) {
    operator fun invoke(endDay: Long): List<DayTotals> {
        val first = endDay - (GameConfig.WEEK_DAYS - 1)
        return (0 until GameConfig.WEEK_DAYS).map { diaryRepository.totalsFor(first + it) }
    }
}
