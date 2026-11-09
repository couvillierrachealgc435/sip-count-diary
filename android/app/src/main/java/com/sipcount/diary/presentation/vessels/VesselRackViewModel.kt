package com.sipcount.diary.presentation.vessels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.Vessel
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class VesselCardUi(
    val id: String,
    val name: String,
    val volumeLabel: String,
    val spriteIndex: Int,
    val colorArgb: Long
)

data class TypeChipUi(
    val id: String,
    val name: String,
    val colorArgb: Long
)

data class VesselEditorUi(
    val id: String?,
    val name: String,
    val volumeMl: Int,
    val spriteIndex: Int,
    val typeId: String,
    val volumeLabel: String
)

data class VesselRackUiState(
    val vessels: List<VesselCardUi> = emptyList(),
    val types: List<TypeChipUi> = emptyList(),
    val editor: VesselEditorUi? = null
)

class VesselRackViewModel(
    private val vesselRepository: VesselRepository,
    private val drinkTypeRepository: DrinkTypeRepository,
    private val preferencesRepository: PreferencesRepository,
    private val formatVolume: FormatVolumeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VesselRackUiState())
    val state: StateFlow<VesselRackUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                vesselRepository.vessels,
                drinkTypeRepository.types,
                preferencesRepository.preferences
            ) { vessels, types, preferences ->
                VesselRackUiState(
                    vessels = vessels.map { vessel ->
                        VesselCardUi(
                            id = vessel.id,
                            name = vessel.name,
                            volumeLabel = formatVolume(vessel.volumeMl, preferences.unit),
                            spriteIndex = vessel.spriteIndex,
                            colorArgb = types.firstOrNull { it.id == vessel.typeId }?.colorArgb
                                ?: 0xFF1E88C7
                        )
                    },
                    types = types.map { TypeChipUi(it.id, it.name, it.colorArgb) }
                )
            }.collect { next ->
                _state.value = next.copy(editor = _state.value.editor)
            }
        }
    }

    fun openNew() {
        val defaultType = _state.value.types.firstOrNull()?.id ?: return
        _state.value = _state.value.copy(
            editor = buildEditor(null, "", 250, 0, defaultType)
        )
    }

    fun openEdit(id: String) {
        val vessel = vesselRepository.find(id) ?: return
        _state.value = _state.value.copy(
            editor = buildEditor(
                vessel.id,
                vessel.name,
                vessel.volumeMl,
                vessel.spriteIndex,
                vessel.typeId
            )
        )
    }

    fun dismissEditor() {
        _state.value = _state.value.copy(editor = null)
    }

    fun changeName(name: String) {
        val editor = _state.value.editor ?: return
        _state.value = _state.value.copy(editor = editor.copy(name = name.take(24)))
    }

    fun changeVolume(deltaSteps: Int) {
        val editor = _state.value.editor ?: return
        val next = (editor.volumeMl + deltaSteps * GameConfig.VESSEL_STEP_ML)
            .coerceIn(GameConfig.MIN_VESSEL_ML, GameConfig.MAX_VESSEL_ML)
        _state.value = _state.value.copy(
            editor = editor.copy(
                volumeMl = next,
                volumeLabel = formatVolume(next, preferencesRepository.preferences.value.unit)
            )
        )
    }

    fun changeSprite(index: Int) {
        val editor = _state.value.editor ?: return
        _state.value = _state.value.copy(editor = editor.copy(spriteIndex = index))
    }

    fun changeType(typeId: String) {
        val editor = _state.value.editor ?: return
        _state.value = _state.value.copy(editor = editor.copy(typeId = typeId))
    }

    fun save() {
        val editor = _state.value.editor ?: return
        viewModelScope.launch {
            val id = editor.id ?: ("v" + System.currentTimeMillis())
            val name = editor.name.ifBlank { fallbackName(editor.spriteIndex) }
            vesselRepository.upsert(
                Vessel(
                    id = id,
                    name = name,
                    volumeMl = editor.volumeMl,
                    spriteIndex = editor.spriteIndex,
                    typeId = editor.typeId
                )
            )
            _state.value = _state.value.copy(editor = null)
        }
    }

    fun delete() {
        val editor = _state.value.editor ?: return
        val id = editor.id ?: return
        viewModelScope.launch {
            vesselRepository.delete(id)
            _state.value = _state.value.copy(editor = null)
        }
    }

    private fun buildEditor(
        id: String?,
        name: String,
        volumeMl: Int,
        spriteIndex: Int,
        typeId: String
    ) = VesselEditorUi(
        id = id,
        name = name,
        volumeMl = volumeMl,
        spriteIndex = spriteIndex,
        typeId = typeId,
        volumeLabel = formatVolume(volumeMl, preferencesRepository.preferences.value.unit)
    )

    private fun fallbackName(spriteIndex: Int): String =
        com.sipcount.diary.data.sample.SampleData.vesselName(spriteIndex)
}
