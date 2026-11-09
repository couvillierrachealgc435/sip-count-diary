package com.sipcount.diary.domain.repository

import com.sipcount.diary.domain.model.DrinkType
import kotlinx.coroutines.flow.StateFlow

interface DrinkTypeRepository {
    val types: StateFlow<List<DrinkType>>
    fun upsert(type: DrinkType)
    fun delete(id: String): Boolean
    fun find(id: String): DrinkType?
}
