package com.sipcount.diary.data.repository

import com.sipcount.diary.data.local.SipPreferences
import com.sipcount.diary.domain.model.DrinkType
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DrinkTypeRepositoryImpl(private val storage: SipPreferences) : DrinkTypeRepository {

    private val state = MutableStateFlow(storage.readTypes())

    override val types: StateFlow<List<DrinkType>> = state.asStateFlow()

    override fun upsert(type: DrinkType) {
        val current = state.value.toMutableList()
        val index = current.indexOfFirst { it.id == type.id }
        if (index >= 0) current[index] = type else current.add(type)
        commit(current)
    }

    override fun delete(id: String): Boolean {
        if (state.value.size <= 1) return false
        commit(state.value.filterNot { it.id == id })
        return true
    }

    override fun find(id: String): DrinkType? = state.value.firstOrNull { it.id == id }

    fun replaceAll(types: List<DrinkType>) {
        commit(types)
    }

    private fun commit(types: List<DrinkType>) {
        storage.writeTypes(types)
        state.value = types
    }
}
