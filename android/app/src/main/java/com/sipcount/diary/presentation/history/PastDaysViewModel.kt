package com.sipcount.diary.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.model.DrinkEntry
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import com.sipcount.diary.domain.usecase.RemoveDrinkUseCase
import com.sipcount.diary.presentation.diary.DiaryRowUi
import com.sipcount.diary.presentation.vessels.VesselCardUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class CalendarCellUi(
    val epochDay: Long,
    val dayOfMonth: Int,
    val fill: Float,
    val metTarget: Boolean,
    val isToday: Boolean
)

data class PastDaysUiState(
    val monthAnchor: Long = 0L,
    val monthIndex: Int = 0,
    val year: Int = 0,
    val cells: List<CalendarCellUi> = emptyList(),
    val leadingBlanks: Int = 0,
    val selectedDay: Long? = null,
    val selectedRows: List<DiaryRowUi> = emptyList(),
    val selectedTotalLabel: String = "0",
    val pickerVessels: List<VesselCardUi> = emptyList(),
    val pickerVisible: Boolean = false,
    val pickerMinute: Int = 480,
    val pendingDeleteId: String? = null
)

class PastDaysViewModel(
    private val diaryRepository: DiaryRepository,
    private val vesselRepository: VesselRepository,
    private val drinkTypeRepository: DrinkTypeRepository,
    private val preferencesRepository: PreferencesRepository,
    private val removeDrink: RemoveDrinkUseCase,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val anchor = MutableStateFlow(Today.firstOfMonth(Today.epochDay()))
    private val selected = MutableStateFlow<Long?>(null)
    private val picker = MutableStateFlow(false)
    private val pickerMinute = MutableStateFlow(480)
    private val pendingDelete = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow(PastDaysUiState())
    val state: StateFlow<PastDaysUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                anchor,
                selected,
                diaryRepository.entries,
                vesselRepository.vessels,
                preferencesRepository.preferences
            ) { monthAnchor, selectedDay, _, vessels, preferences ->
                val today = Today.epochDay()
                val length = Today.lengthOfMonth(monthAnchor)
                val types = drinkTypeRepository.types.value
                val cells = (0 until length).map { offset ->
                    val day = monthAnchor + offset
                    val totals = diaryRepository.totalsFor(day)
                    CalendarCellUi(
                        epochDay = day,
                        dayOfMonth = Today.dayOfMonth(day),
                        fill = if (preferences.targetMl <= 0) {
                            0f
                        } else {
                            (totals.totalMl.toFloat() / preferences.targetMl).coerceIn(0f, 1f)
                        },
                        metTarget = totals.totalMl >= preferences.targetMl && totals.totalMl > 0,
                        isToday = day == today
                    )
                }
                val rows = selectedDay?.let { day ->
                    diaryRepository.entriesFor(day).map { entry ->
                        toRow(entry, vessels.firstOrNull { it.id == entry.vesselId }?.spriteIndex ?: 0,
                            vessels.firstOrNull { it.id == entry.vesselId }?.name.orEmpty(),
                            types.firstOrNull { it.id == entry.typeId }?.name.orEmpty(),
                            types.firstOrNull { it.id == entry.typeId }?.colorArgb ?: 0xFF1E88C7,
                            preferences.unit
                        )
                    }
                }.orEmpty()
                PastDaysUiState(
                    monthAnchor = monthAnchor,
                    monthIndex = Today.monthIndex(monthAnchor),
                    year = Today.year(monthAnchor),
                    cells = cells,
                    leadingBlanks = Today.weekdayIndex(monthAnchor),
                    selectedDay = selectedDay,
                    selectedRows = rows,
                    selectedTotalLabel = formatVolume(
                        selectedDay?.let { diaryRepository.totalsFor(it).totalMl } ?: 0,
                        preferences.unit
                    ),
                    pickerVessels = vessels.map { vessel ->
                        VesselCardUi(
                            id = vessel.id,
                            name = vessel.name,
                            volumeLabel = formatVolume(vessel.volumeMl, preferences.unit),
                            spriteIndex = vessel.spriteIndex,
                            colorArgb = types.firstOrNull { it.id == vessel.typeId }?.colorArgb
                                ?: 0xFF1E88C7
                        )
                    },
                    pickerVisible = picker.value,
                    pickerMinute = pickerMinute.value,
                    pendingDeleteId = pendingDelete.value
                )
            }.collect { next ->
                _state.value = next
            }
        }
    }

    private fun toRow(
        entry: DrinkEntry,
        spriteIndex: Int,
        vesselName: String,
        typeName: String,
        colorArgb: Long,
        unit: com.sipcount.diary.domain.model.VolumeUnit
    ) = DiaryRowUi(
        id = entry.id,
        title = vesselName,
        subtitle = typeName,
        time = Today.clock(entry.minuteOfDay),
        volumeLabel = formatVolume(entry.volumeMl, unit),
        spriteIndex = spriteIndex,
        colorArgb = colorArgb
    )

    fun shiftMonth(months: Long) {
        anchor.value = Today.shiftMonths(anchor.value, months)
        selected.value = null
        refresh()
    }

    fun selectDay(epochDay: Long) {
        selected.value = if (selected.value == epochDay) null else epochDay
        refresh()
    }

    fun openPicker() {
        picker.value = true
        refresh()
    }

    fun dismissPicker() {
        picker.value = false
        refresh()
    }

    fun shiftPickerMinute(deltaMinutes: Int) {
        pickerMinute.value = (pickerMinute.value + deltaMinutes).coerceIn(0, 23 * 60 + 45)
        refresh()
    }

    fun addEntry(vesselId: String) {
        val day = selected.value ?: return
        viewModelScope.launch {
            val vessel = vesselRepository.find(vesselId) ?: return@launch
            diaryRepository.add(
                DrinkEntry(
                    id = "h" + day + "_" + pickerMinute.value + "_" + vessel.id,
                    epochDay = day,
                    minuteOfDay = pickerMinute.value,
                    vesselId = vessel.id,
                    typeId = vessel.typeId,
                    volumeMl = vessel.volumeMl
                )
            )
            picker.value = false
            refresh()
        }
    }

    fun askDelete(entryId: String) {
        pendingDelete.value = entryId
        refresh()
    }

    fun dismissDelete() {
        pendingDelete.value = null
        refresh()
    }

    fun confirmDelete() {
        val id = pendingDelete.value ?: return
        viewModelScope.launch {
            removeDrink(id)
            pendingDelete.value = null
            refresh()
        }
    }

    private fun refresh() {
        _state.value = _state.value.copy(
            selectedDay = selected.value,
            pickerVisible = picker.value,
            pickerMinute = pickerMinute.value,
            pendingDeleteId = pendingDelete.value
        )
    }
}
