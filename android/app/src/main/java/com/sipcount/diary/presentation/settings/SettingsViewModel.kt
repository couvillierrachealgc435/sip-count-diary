package com.sipcount.diary.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.model.VolumeUnit
import com.sipcount.diary.domain.repository.DiaryExporter
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val targetMl: Int = GameConfig.DEFAULT_TARGET_ML,
    val targetLabel: String = "0",
    val unit: VolumeUnit = VolumeUnit.ML,
    val darkTheme: Boolean = false,
    val quickTargets: List<Int> = listOf(1200, 1600, 2000, 2500),
    val quickLabels: List<String> = emptyList(),
    val exportDone: Boolean = false
)

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val diaryRepository: DiaryRepository,
    private val vesselRepository: VesselRepository,
    private val diaryExporter: DiaryExporter,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val exportFlag = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.preferences,
                exportFlag
            ) { preferences, exported ->
                val quick = listOf(1200, 1600, 2000, 2500)
                SettingsUiState(
                    targetMl = preferences.targetMl,
                    targetLabel = formatVolume(preferences.targetMl, preferences.unit),
                    unit = preferences.unit,
                    darkTheme = preferences.darkTheme,
                    quickTargets = quick,
                    quickLabels = quick.map { formatVolume(it, preferences.unit) },
                    exportDone = exported
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    fun shiftTarget(steps: Int) {
        val current = _state.value
        val step = formatVolume.step(current.unit)
        preferencesRepository.setTargetMl(current.targetMl + steps * step)
    }

    fun setTarget(targetMl: Int) {
        preferencesRepository.setTargetMl(targetMl)
    }

    fun setUnit(unit: VolumeUnit) {
        preferencesRepository.setUnit(unit)
    }

    fun setDarkTheme(darkTheme: Boolean) {
        preferencesRepository.setDarkTheme(darkTheme)
    }

    fun exportDiary(label: String, header: String, unitSuffix: String) {
        viewModelScope.launch {
            val unit = _state.value.unit
            val days = diaryRepository.entries.value
                .groupBy { it.epochDay }
                .toSortedMap()
            val vessels = vesselRepository.vessels.value
            val builder = StringBuilder()
            builder.append(header).append('\n')
            days.forEach { (day, entries) ->
                builder.append(Today.dayOfMonth(day))
                builder.append('.')
                builder.append(Today.monthIndex(day) + 1)
                builder.append('.')
                builder.append(Today.year(day))
                builder.append('\n')
                entries.sortedBy { it.minuteOfDay }.forEach { entry ->
                    val name = vessels.firstOrNull { it.id == entry.vesselId }?.name.orEmpty()
                    builder.append("  ")
                    builder.append(Today.clock(entry.minuteOfDay))
                    builder.append("  ")
                    builder.append(name)
                    builder.append("  ")
                    builder.append(formatVolume(entry.volumeMl, unit))
                    builder.append(' ')
                    builder.append(unitSuffix)
                    builder.append('\n')
                }
            }
            diaryExporter.export(label, builder.toString())
            exportFlag.value = true
        }
    }

    fun dismissExport() {
        exportFlag.value = false
    }
}
