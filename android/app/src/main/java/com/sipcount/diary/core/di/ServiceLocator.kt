package com.sipcount.diary.core.di

import android.content.Context
import com.sipcount.diary.core.util.Today
import com.sipcount.diary.data.local.SipPreferences
import com.sipcount.diary.data.local.SystemClipboard
import com.sipcount.diary.data.repository.DiaryRepositoryImpl
import com.sipcount.diary.data.repository.DrinkTypeRepositoryImpl
import com.sipcount.diary.data.repository.PreferencesRepositoryImpl
import com.sipcount.diary.data.repository.VesselRepositoryImpl
import com.sipcount.diary.data.sample.SampleData
import com.sipcount.diary.domain.repository.DiaryExporter
import com.sipcount.diary.domain.repository.DiaryRepository
import com.sipcount.diary.domain.repository.DrinkTypeRepository
import com.sipcount.diary.domain.repository.PreferencesRepository
import com.sipcount.diary.domain.repository.VesselRepository
import com.sipcount.diary.domain.usecase.AddDrinkUseCase
import com.sipcount.diary.domain.usecase.BuildWeekChartUseCase
import com.sipcount.diary.domain.usecase.FormatVolumeUseCase
import com.sipcount.diary.domain.usecase.RemoveDrinkUseCase

object ServiceLocator {

    private var ready = false

    private var storageRef: SipPreferences? = null
    private var vesselRef: VesselRepositoryImpl? = null
    private var typeRef: DrinkTypeRepositoryImpl? = null
    private var diaryRef: DiaryRepositoryImpl? = null
    private var preferencesRef: PreferencesRepositoryImpl? = null
    private var exporterRef: DiaryExporter? = null

    val vesselRepository: VesselRepository get() = requireNotNull(vesselRef)
    val drinkTypeRepository: DrinkTypeRepository get() = requireNotNull(typeRef)
    val diaryRepository: DiaryRepository get() = requireNotNull(diaryRef)
    val preferencesRepository: PreferencesRepository get() = requireNotNull(preferencesRef)
    val diaryExporter: DiaryExporter get() = requireNotNull(exporterRef)

    val addDrinkUseCase: AddDrinkUseCase get() = AddDrinkUseCase(diaryRepository, preferencesRepository)
    val removeDrinkUseCase: RemoveDrinkUseCase get() = RemoveDrinkUseCase(diaryRepository)
    val buildWeekChartUseCase: BuildWeekChartUseCase get() = BuildWeekChartUseCase(diaryRepository)
    val formatVolumeUseCase: FormatVolumeUseCase get() = FormatVolumeUseCase()

    fun init(context: Context) {
        if (ready) return
        val storage = SipPreferences(context.applicationContext)
        val vessels = VesselRepositoryImpl(storage)
        val types = DrinkTypeRepositoryImpl(storage)
        val diary = DiaryRepositoryImpl(storage)
        if (!storage.isSeeded()) {
            types.replaceAll(SampleData.types)
            vessels.replaceAll(SampleData.vessels)
            diary.replaceAll(SampleData.seedEntries(Today.epochDay()))
            storage.writePreferences(SampleData.preferences)
            storage.markSeeded()
        }
        storageRef = storage
        vesselRef = vessels
        typeRef = types
        diaryRef = diary
        preferencesRef = PreferencesRepositoryImpl(storage)
        exporterRef = SystemClipboard(context.applicationContext)
        ready = true
    }
}
