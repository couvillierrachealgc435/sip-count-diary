package com.sipcount.diary.data.repository

import com.sipcount.diary.data.local.SipPreferences
import com.sipcount.diary.domain.model.Vessel
import com.sipcount.diary.domain.repository.VesselRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VesselRepositoryImpl(private val storage: SipPreferences) : VesselRepository {

    private val state = MutableStateFlow(storage.readVessels())

    override val vessels: StateFlow<List<Vessel>> = state.asStateFlow()

    override fun upsert(vessel: Vessel) {
        val current = state.value.toMutableList()
        val index = current.indexOfFirst { it.id == vessel.id }
        if (index >= 0) current[index] = vessel else current.add(vessel)
        commit(current)
    }

    override fun delete(id: String) {
        commit(state.value.filterNot { it.id == id })
    }

    override fun find(id: String): Vessel? = state.value.firstOrNull { it.id == id }

    fun replaceAll(vessels: List<Vessel>) {
        commit(vessels)
    }

    private fun commit(vessels: List<Vessel>) {
        storage.writeVessels(vessels)
        state.value = vessels
    }
}
