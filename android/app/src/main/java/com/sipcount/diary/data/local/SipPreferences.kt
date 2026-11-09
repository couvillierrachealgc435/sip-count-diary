package com.sipcount.diary.data.local

import android.content.Context
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.domain.model.DrinkEntry
import com.sipcount.diary.domain.model.DrinkType
import com.sipcount.diary.domain.model.Preferences
import com.sipcount.diary.domain.model.Vessel
import com.sipcount.diary.domain.model.VolumeUnit
import org.json.JSONArray
import org.json.JSONObject

class SipPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("sip_count_diary", Context.MODE_PRIVATE)

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun readVessels(): List<Vessel> = readArray(KEY_VESSELS) { item ->
        Vessel(
            id = item.getString("id"),
            name = item.getString("name"),
            volumeMl = item.getInt("volumeMl"),
            spriteIndex = item.getInt("spriteIndex"),
            typeId = item.getString("typeId")
        )
    }

    fun writeVessels(vessels: List<Vessel>) {
        writeArray(KEY_VESSELS, vessels) { vessel ->
            JSONObject()
                .put("id", vessel.id)
                .put("name", vessel.name)
                .put("volumeMl", vessel.volumeMl)
                .put("spriteIndex", vessel.spriteIndex)
                .put("typeId", vessel.typeId)
        }
    }

    fun readTypes(): List<DrinkType> = readArray(KEY_TYPES) { item ->
        DrinkType(
            id = item.getString("id"),
            name = item.getString("name"),
            colorArgb = item.getString("colorArgb").toLong()
        )
    }

    fun writeTypes(types: List<DrinkType>) {
        writeArray(KEY_TYPES, types) { type ->
            JSONObject()
                .put("id", type.id)
                .put("name", type.name)
                .put("colorArgb", type.colorArgb.toString())
        }
    }

    fun readEntries(): List<DrinkEntry> = readArray(KEY_ENTRIES) { item ->
        DrinkEntry(
            id = item.getString("id"),
            epochDay = item.getString("epochDay").toLong(),
            minuteOfDay = item.getInt("minuteOfDay"),
            vesselId = item.getString("vesselId"),
            typeId = item.getString("typeId"),
            volumeMl = item.getInt("volumeMl")
        )
    }

    fun writeEntries(entries: List<DrinkEntry>) {
        writeArray(KEY_ENTRIES, entries) { entry ->
            JSONObject()
                .put("id", entry.id)
                .put("epochDay", entry.epochDay.toString())
                .put("minuteOfDay", entry.minuteOfDay)
                .put("vesselId", entry.vesselId)
                .put("typeId", entry.typeId)
                .put("volumeMl", entry.volumeMl)
        }
    }

    fun readPreferences(): Preferences = Preferences(
        targetMl = prefs.getInt(KEY_TARGET, GameConfig.DEFAULT_TARGET_ML),
        unit = if (prefs.getString(KEY_UNIT, UNIT_ML) == UNIT_OZ) VolumeUnit.OZ else VolumeUnit.ML,
        darkTheme = prefs.getBoolean(KEY_DARK, false)
    )

    fun writePreferences(preferences: Preferences) {
        prefs.edit()
            .putInt(KEY_TARGET, preferences.targetMl)
            .putString(KEY_UNIT, if (preferences.unit == VolumeUnit.OZ) UNIT_OZ else UNIT_ML)
            .putBoolean(KEY_DARK, preferences.darkTheme)
            .apply()
    }

    private fun <T> readArray(key: String, map: (JSONObject) -> T): List<T> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val result = ArrayList<T>(array.length())
            for (index in 0 until array.length()) {
                result.add(map(array.getJSONObject(index)))
            }
            result
        } catch (error: Exception) {
            emptyList()
        }
    }

    private fun <T> writeArray(key: String, items: List<T>, map: (T) -> JSONObject) {
        val array = JSONArray()
        items.forEach { array.put(map(it)) }
        prefs.edit().putString(key, array.toString()).apply()
    }

    private companion object {
        const val KEY_SEEDED = "seeded"
        const val KEY_VESSELS = "vessels"
        const val KEY_TYPES = "types"
        const val KEY_ENTRIES = "entries"
        const val KEY_TARGET = "target_ml"
        const val KEY_UNIT = "unit"
        const val KEY_DARK = "dark_theme"
        const val UNIT_ML = "ml"
        const val UNIT_OZ = "oz"
    }
}
