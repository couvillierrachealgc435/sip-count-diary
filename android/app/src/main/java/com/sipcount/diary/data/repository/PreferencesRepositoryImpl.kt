package com.sipcount.diary.data.repository

import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.data.local.SipPreferences
import com.sipcount.diary.domain.model.Preferences
import com.sipcount.diary.domain.model.VolumeUnit
import com.sipcount.diary.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepositoryImpl(private val storage: SipPreferences) : PreferencesRepository {

    private val state = MutableStateFlow(storage.readPreferences())

    override val preferences: StateFlow<Preferences> = state.asStateFlow()

    override fun setTargetMl(targetMl: Int) {
        commit(
            state.value.copy(
                targetMl = targetMl.coerceIn(GameConfig.MIN_TARGET_ML, GameConfig.MAX_TARGET_ML)
            )
        )
    }

    override fun setUnit(unit: VolumeUnit) {
        commit(state.value.copy(unit = unit))
    }

    override fun setDarkTheme(darkTheme: Boolean) {
        commit(state.value.copy(darkTheme = darkTheme))
    }

    private fun commit(preferences: Preferences) {
        storage.writePreferences(preferences)
        state.value = preferences
    }
}
