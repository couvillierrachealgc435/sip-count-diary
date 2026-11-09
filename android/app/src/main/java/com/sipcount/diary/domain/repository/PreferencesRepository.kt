package com.sipcount.diary.domain.repository

import com.sipcount.diary.domain.model.Preferences
import com.sipcount.diary.domain.model.VolumeUnit
import kotlinx.coroutines.flow.StateFlow

interface PreferencesRepository {
    val preferences: StateFlow<Preferences>
    fun setTargetMl(targetMl: Int)
    fun setUnit(unit: VolumeUnit)
    fun setDarkTheme(darkTheme: Boolean)
}
