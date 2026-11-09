package com.sipcount.diary.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import com.sipcount.diary.domain.usecase.RemoveDrinkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DiaryRowUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val volumeLabel: String,
    val spriteIndex: Int,
    val colorArgb: Long
)

data class DiaryUiState(
    val rows: List<DiaryRowUi> = emptyList(),
    val pendingDeleteId: String? = null
)

class DiaryViewModel(
    private val diaryRepository: DiaryRepository,
    private val vesselRepository: VesselRepository,
    private val drinkTypeRepository: DrinkTypeRepository,
    private val preferencesRepository: PreferencesRepository,
    private val removeDrink: RemoveDrinkUseCase,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DiaryUiState())
    val state: StateFlow<DiaryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                diaryRepository.entries,
                vesselRepository.vessels,
                drinkTypeRepository.types,
                preferencesRepository.preferences
            ) { _, vessels, types, preferences ->
                diaryRepository.entriesFor(Today.epochDay()).map { entry ->
                    val vessel = vessels.firstOrNull { it.id == entry.vesselId }
                    val type = types.firstOrNull { it.id == entry.typeId }
                    DiaryRowUi(
                        id = entry.id,
                        title = vessel?.name.orEmpty(),
                        subtitle = type?.name.orEmpty(),
                        time = Today.clock(entry.minuteOfDay),
                        volumeLabel = formatVolume(entry.volumeMl, preferences.unit),
                        spriteIndex = vessel?.spriteIndex ?: 0,
                        colorArgb = type?.colorArgb ?: 0xFF1E88C7
                    )
                }
            }.collect { rows ->
                _state.value = _state.value.copy(rows = rows)
            }
        }
    }

    fun askDelete(entryId: String) {
        _state.value = _state.value.copy(pendingDeleteId = entryId)
    }

    fun dismissDelete() {
        _state.value = _state.value.copy(pendingDeleteId = null)
    }

    fun confirmDelete() {
        val id = _state.value.pendingDeleteId ?: return
        viewModelScope.launch {
            removeDrink(id)
            _state.value = _state.value.copy(pendingDeleteId = null)
        }
    }
}
