package com.sipcount.diary.presentation.game

import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.VolumeUnit

data class VesselUi(
    val id: String,
    val name: String,
    val spriteIndex: Int,
    val volumeLabel: String,
    val colorArgb: Long
)

data class GameUiState(
    val vessels: List<VesselUi> = emptyList(),
    val totalMl: Int = 0,
    val targetMl: Int = GameConfig.DEFAULT_TARGET_ML,
    val totalLabel: String = "0",
    val targetLabel: String = "0",
    val remainingLabel: String = "0",
    val unit: VolumeUnit = VolumeUnit.ML,
    val entryCount: Int = 0,
    val sloshToken: Int = 0,
    val epochDay: Long = 0L
) {
    val hasVessels: Boolean get() = vessels.isNotEmpty()

    val canUndo: Boolean get() = entryCount > 0

    val fraction: Float
        get() = if (targetMl <= 0) 0f else (totalMl.toFloat() / targetMl).coerceIn(0f, 1f)

    val targetReached: Boolean get() = totalMl >= targetMl && totalMl > 0
}

sealed interface GameEvent {
    data object TargetReached : GameEvent
}
