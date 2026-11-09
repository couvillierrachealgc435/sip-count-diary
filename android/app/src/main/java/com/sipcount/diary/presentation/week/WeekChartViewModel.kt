package com.sipcount.diary.presentation.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.usecase.BuildWeekChartUseCase
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class WeekBarSegment(
    val colorArgb: Long,
    val share: Float
)

data class WeekBarUi(
    val epochDay: Long,
    val weekdayIndex: Int,
    val totalMl: Int,
    val totalLabel: String,
    val heightShare: Float,
    val segments: List<WeekBarSegment>,
    val isToday: Boolean
)

data class WeekChartUiState(
    val bars: List<WeekBarUi> = emptyList(),
    val weekTotalLabel: String = "0",
    val averageLabel: String = "0",
    val weekTotalMl: Int = 0,
    val averageMl: Int = 0,
    val targetShare: Float = 0f,
    val selectedIndex: Int = -1
)

class WeekChartViewModel(
    private val diaryRepository: DiaryRepository,
    private val drinkTypeRepository: DrinkTypeRepository,
    private val preferencesRepository: PreferencesRepository,
    private val buildWeekChart: BuildWeekChartUseCase,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WeekChartUiState())
    val state: StateFlow<WeekChartUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                diaryRepository.entries,
                drinkTypeRepository.types,
                preferencesRepository.preferences
            ) { _, types, preferences ->
                val today = Today.epochDay()
                val days = buildWeekChart(today)
                val peak = maxOf(days.maxOfOrNull { it.totalMl } ?: 0, preferences.targetMl)
                val weekTotal = days.sumOf { it.totalMl }
                val average = weekTotal / GameConfig.WEEK_DAYS
                WeekChartUiState(
                    bars = days.map { day ->
                        WeekBarUi(
                            epochDay = day.epochDay,
                            weekdayIndex = Today.weekdayIndex(day.epochDay),
                            totalMl = day.totalMl,
                            totalLabel = formatVolume(day.totalMl, preferences.unit),
                            heightShare = if (peak <= 0) 0f else day.totalMl.toFloat() / peak,
                            segments = day.perType.entries.mapNotNull { bucket ->
                                val type = types.firstOrNull { it.id == bucket.key }
                                    ?: return@mapNotNull null
                                WeekBarSegment(
                                    colorArgb = type.colorArgb,
                                    share = if (day.totalMl <= 0) {
                                        0f
                                    } else {
                                        bucket.value.toFloat() / day.totalMl
                                    }
                                )
                            },
                            isToday = day.epochDay == today
                        )
                    },
                    weekTotalLabel = formatVolume(weekTotal, preferences.unit),
                    averageLabel = formatVolume(average, preferences.unit),
                    weekTotalMl = weekTotal,
                    averageMl = average,
                    targetShare = if (peak <= 0) 0f else preferences.targetMl.toFloat() / peak,
                    selectedIndex = _state.value.selectedIndex
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun select(index: Int) {
        _state.value = _state.value.copy(
            selectedIndex = if (_state.value.selectedIndex == index) -1 else index
        )
    }
}
