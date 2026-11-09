package com.sipcount.diary.presentation.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.domain.model.DrinkType
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DrinkTypeRowUi(
    val id: String,
    val name: String,
    val colorArgb: Long,
    val todayCount: Int
)

data class DrinkTypeEditorUi(
    val id: String?,
    val name: String,
    val colorArgb: Long,
    val canDelete: Boolean
)

data class DrinkTypesUiState(
    val rows: List<DrinkTypeRowUi> = emptyList(),
    val editor: DrinkTypeEditorUi? = null
) {
    companion object {
        val palette: List<Long> = listOf(
            0xFF1E88C7,
            0xFF00A6CB,
            0xFF4CAF84,
            0xFF8D5B3F,
            0xFFFF9F43,
            0xFFFF6F91,
            0xFF9B6BD6,
            0xFF46687C
        )
    }
}

class DrinkTypesViewModel(
    private val drinkTypeRepository: DrinkTypeRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DrinkTypesUiState())
    val state: StateFlow<DrinkTypesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                drinkTypeRepository.types,
                diaryRepository.entries
            ) { types, _ ->
                val today = diaryRepository.entriesFor(Today.epochDay())
                types.map { type ->
                    DrinkTypeRowUi(
                        id = type.id,
                        name = type.name,
                        colorArgb = type.colorArgb,
                        todayCount = today.count { it.typeId == type.id }
                    )
                }
            }.collect { rows ->
                _state.value = _state.value.copy(rows = rows)
            }
        }
    }

    fun openNew() {
        _state.value = _state.value.copy(
            editor = DrinkTypeEditorUi(
                id = null,
                name = "",
                colorArgb = DrinkTypesUiState.palette.first(),
                canDelete = false
            )
        )
    }

    fun openEdit(id: String) {
        val type = drinkTypeRepository.find(id) ?: return
        _state.value = _state.value.copy(
            editor = DrinkTypeEditorUi(
                id = type.id,
                name = type.name,
                colorArgb = type.colorArgb,
                canDelete = _state.value.rows.size > 1
            )
        )
    }

    fun dismissEditor() {
        _state.value = _state.value.copy(editor = null)
    }

    fun changeName(name: String) {
        val editor = _state.value.editor ?: return
        _state.value = _state.value.copy(editor = editor.copy(name = name.take(18)))
    }

    fun changeColor(colorArgb: Long) {
        val editor = _state.value.editor ?: return
        _state.value = _state.value.copy(editor = editor.copy(colorArgb = colorArgb))
    }

    fun save() {
        val editor = _state.value.editor ?: return
        viewModelScope.launch {
            val id = editor.id ?: ("t" + System.currentTimeMillis())
            val name = editor.name.ifBlank { id.takeLast(4) }
            drinkTypeRepository.upsert(DrinkType(id, name, editor.colorArgb))
            _state.value = _state.value.copy(editor = null)
        }
    }

    fun delete() {
        val editor = _state.value.editor ?: return
        val id = editor.id ?: return
        viewModelScope.launch {
            drinkTypeRepository.delete(id)
            _state.value = _state.value.copy(editor = null)
        }
    }
}
