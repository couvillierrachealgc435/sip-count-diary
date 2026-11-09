package com.sipcount.diary.data.sample

import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.DrinkEntry
import com.sipcount.diary.domain.model.DrinkType
import com.sipcount.diary.domain.model.Preferences
import com.sipcount.diary.domain.model.Vessel
import com.sipcount.diary.domain.model.VolumeUnit

object SampleData {

    const val TYPE_WATER = "water"
    const val TYPE_TEA = "tea"
    const val TYPE_COFFEE = "coffee"
    const val TYPE_JUICE = "juice"
    const val TYPE_OTHER = "other"

    val types: List<DrinkType> = listOf(
        DrinkType(TYPE_WATER, "Water", 0xFF1E88C7),
        DrinkType(TYPE_TEA, "Tea", 0xFF4CAF84),
        DrinkType(TYPE_COFFEE, "Coffee", 0xFF8D5B3F),
        DrinkType(TYPE_JUICE, "Juice", 0xFFFF9F43),
        DrinkType(TYPE_OTHER, "Other", 0xFFFF6F91)
    )

    val vessels: List<Vessel> = listOf(
        Vessel("v_small_glass", "Small Glass", 200, 0, TYPE_WATER),
        Vessel("v_tea_cup", "Tea Cup", 250, 1, TYPE_TEA),
        Vessel("v_coffee_mug", "Coffee Mug", 180, 2, TYPE_COFFEE),
        Vessel("v_juice_carton", "Juice Carton", 330, 3, TYPE_JUICE),
        Vessel("v_sport_bottle", "Sport Bottle", 750, 4, TYPE_WATER)
    )

    val preferences = Preferences(
        targetMl = GameConfig.DEFAULT_TARGET_ML,
        unit = VolumeUnit.ML,
        darkTheme = false
    )

    fun seedEntries(today: Long): List<DrinkEntry> {
        val yesterday = today - 1
        return listOf(
            DrinkEntry("seed_1", yesterday, 8 * 60 + 15, "v_small_glass", TYPE_WATER, 200),
            DrinkEntry("seed_2", yesterday, 13 * 60 + 40, "v_tea_cup", TYPE_TEA, 250),
            DrinkEntry("seed_3", yesterday, 19 * 60 + 5, "v_sport_bottle", TYPE_WATER, 750)
        )
    }

    fun vesselName(index: Int): String = vessels[index.coerceIn(0, vessels.lastIndex)].name
}
