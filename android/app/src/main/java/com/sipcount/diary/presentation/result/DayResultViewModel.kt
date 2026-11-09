package com.sipcount.diary.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.VolumeUnit
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class TypeSegment(
    val name: String,
    val colorArgb: Long,
    val share: Float,
    val percent: Int,
    val spriteIndex: Int
)

data class DayResultUiState(
    val totalMl: Int = 0,
    val targetMl: Int = GameConfig.DEFAULT_TARGET_ML,
    val totalLabel: String = "0",
    val targetLabel: String = "0",
    val biggestLabel: String = "0",
    val biggestMl: Int = 0,
    val unit: VolumeUnit = VolumeUnit.ML,
    val drinkCount: Int = 0,
    val typeCount: Int = 0,
    val segments: List<TypeSegment> = emptyList()
) {
    val targetReached: Boolean get() = totalMl >= targetMl && totalMl > 0
}

class DayResultViewModel(
    private val diaryRepository: DiaryRepository,
    private val drinkTypeRepository: DrinkTypeRepository,
    private val vesselRepository: VesselRepository,
    private val preferencesRepository: PreferencesRepository,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val day = MutableStateFlow(0L)

    private val _state = MutableStateFlow(DayResultUiState())
    val state: StateFlow<DayResultUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                day,
                diaryRepository.entries,
                drinkTypeRepository.types,
                vesselRepository.vessels,
                preferencesRepository.preferences
            ) { epochDay, _, types, vessels, preferences ->
                val entries = diaryRepository.entriesFor(epochDay)
                val totals = diaryRepository.totalsFor(epochDay)
                val biggest = entries.maxOfOrNull { it.volumeMl } ?: 0
                val segments = totals.perType.entries.mapNotNull { bucket ->
                    val type = types.firstOrNull { it.id == bucket.key } ?: return@mapNotNull null
                    val share = if (totals.totalMl <= 0) 0f else bucket.value.toFloat() / totals.totalMl
                    TypeSegment(
                        name = type.name,
                        colorArgb = type.colorArgb,
                        share = share,
                        percent = (share * 100f).toInt(),
                        spriteIndex = vessels.firstOrNull { it.typeId == bucket.key }?.spriteIndex ?: 0
                    )
                }.sortedByDescending { it.share }.take(GameConfig.LEGEND_LIMIT)
                DayResultUiState(
                    totalMl = totals.totalMl,
                    targetMl = preferences.targetMl,
                    totalLabel = formatVolume(totals.totalMl, preferences.unit),
                    targetLabel = formatVolume(preferences.targetMl, preferences.unit),
                    biggestLabel = formatVolume(biggest, preferences.unit),
                    biggestMl = biggest,
                    unit = preferences.unit,
                    drinkCount = totals.count,
                    typeCount = totals.perType.size,
                    segments = segments
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun select(epochDay: Long) {
        day.value = epochDay
    }
}
