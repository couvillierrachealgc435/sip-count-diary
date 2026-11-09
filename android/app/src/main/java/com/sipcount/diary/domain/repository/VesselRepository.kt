package com.sipcount.diary.domain.repository

import com.sipcount.diary.domain.model.Vessel
import kotlinx.coroutines.flow.StateFlow

interface VesselRepository {
    val vessels: StateFlow<List<Vessel>>
    fun upsert(vessel: Vessel)
    fun delete(id: String)
    fun find(id: String): Vessel?
}
