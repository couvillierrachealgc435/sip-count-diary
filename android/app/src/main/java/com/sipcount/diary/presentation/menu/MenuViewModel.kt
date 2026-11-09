package com.sipcount.diary.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MenuViewModel(
    private val diaryRepository: DiaryRepository,
    private val vesselRepository: VesselRepository,
    private val preferencesRepository: PreferencesRepository,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                diaryRepository.entries,
                vesselRepository.vessels,
                preferencesRepository.preferences
            ) { _, vessels, preferences ->
                val today = Today.epochDay()
                val totals = diaryRepository.totalsFor(today)
                val last = diaryRepository.entriesFor(today).maxByOrNull { it.minuteOfDay }
                MenuUiState(
                    totalMl = totals.totalMl,
                    targetMl = preferences.targetMl,
                    totalLabel = formatVolume(totals.totalMl, preferences.unit),
                    targetLabel = formatVolume(preferences.targetMl, preferences.unit),
                    unit = preferences.unit,
                    drinkCount = totals.count,
                    lastSipTime = last?.let { Today.clock(it.minuteOfDay) }.orEmpty(),
                    vesselCount = vessels.size
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }
}
