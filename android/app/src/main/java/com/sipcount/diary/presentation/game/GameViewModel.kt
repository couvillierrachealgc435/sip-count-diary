package com.sipcount.diary.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.AddDrinkUseCase
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import com.sipcount.diary.domain.usecase.RemoveDrinkUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val diaryRepository: DiaryRepository,
    private val vesselRepository: VesselRepository,
    private val drinkTypeRepository: DrinkTypeRepository,
    private val preferencesRepository: PreferencesRepository,
    private val addDrink: AddDrinkUseCase,
    private val removeDrink: RemoveDrinkUseCase,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState(epochDay = Today.epochDay()))
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = _events.receiveAsFlow()

    private var slosh = 0

    init {
        viewModelScope.launch {
            combine(
                diaryRepository.entries,
                vesselRepository.vessels,
                drinkTypeRepository.types,
                preferencesRepository.preferences
            ) { _, vessels, types, preferences ->
                val today = Today.epochDay()
                val totals = diaryRepository.totalsFor(today)
                val remaining = (preferences.targetMl - totals.totalMl).coerceAtLeast(0)
                GameUiState(
                    vessels = vessels.map { vessel ->
                        VesselUi(
                            id = vessel.id,
                            name = vessel.name,
                            spriteIndex = vessel.spriteIndex,
                            volumeLabel = formatVolume(vessel.volumeMl, preferences.unit),
                            colorArgb = types.firstOrNull { it.id == vessel.typeId }?.colorArgb
                                ?: 0xFF1E88C7
                        )
                    },
                    totalMl = totals.totalMl,
                    targetMl = preferences.targetMl,
                    totalLabel = formatVolume(totals.totalMl, preferences.unit),
                    targetLabel = formatVolume(preferences.targetMl, preferences.unit),
                    remainingLabel = formatVolume(remaining, preferences.unit),
                    unit = preferences.unit,
                    entryCount = totals.count,
                    sloshToken = slosh,
                    epochDay = today
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun onVesselTapped(vesselId: String) {
        viewModelScope.launch {
            val vessel = vesselRepository.find(vesselId) ?: return@launch
            slosh += 1
            val result = addDrink(vessel, Today.epochDay(), Today.minuteOfDay())
            _state.value = _state.value.copy(sloshToken = slosh)
            if (result.crossedTarget) {
                _events.send(GameEvent.TargetReached)
            }
        }
    }

    fun pause() {
        slosh = 0
        _state.value = _state.value.copy(sloshToken = 0)
    }

    fun undoLast() {
        viewModelScope.launch {
            removeDrink.removeLast(Today.epochDay())
        }
    }
}
