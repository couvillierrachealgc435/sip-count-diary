package com.sipcount.diary.domain.usecase

import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.VolumeUnit
import kotlin.math.roundToInt

class FormatVolumeUseCase {
    operator fun invoke(ml: Int, unit: VolumeUnit): String = when (unit) {
        VolumeUnit.ML -> ml.toString()
        VolumeUnit.OZ -> {
            val tenths = (ml / GameConfig.ML_PER_OZ * 10f).roundToInt()
            val whole = tenths / 10
            val fraction = tenths % 10
            whole.toString() + "." + fraction.toString()
        }
    }

    fun step(unit: VolumeUnit): Int = when (unit) {
        VolumeUnit.ML -> GameConfig.TARGET_STEP_ML
        VolumeUnit.OZ -> GameConfig.TARGET_STEP_OZ_ML
    }
}
