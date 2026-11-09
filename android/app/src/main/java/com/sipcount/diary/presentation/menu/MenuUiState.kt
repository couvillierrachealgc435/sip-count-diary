package com.sipcount.diary.presentation.menu

import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.VolumeUnit

data class MenuUiState(
    val totalMl: Int = 0,
    val targetMl: Int = GameConfig.DEFAULT_TARGET_ML,
    val totalLabel: String = "0",
    val targetLabel: String = "0",
    val unit: VolumeUnit = VolumeUnit.ML,
    val drinkCount: Int = 0,
    val lastSipTime: String = "",
    val vesselCount: Int = 0
) {
    val fraction: Float
        get() = if (targetMl <= 0) 0f else (totalMl.toFloat() / targetMl).coerceIn(0f, 1f)

    val targetReached: Boolean get() = totalMl >= targetMl && totalMl > 0

    val hasActivity: Boolean get() = drinkCount > 0 && lastSipTime.isNotEmpty()
}
