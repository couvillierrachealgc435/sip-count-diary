package com.sipcount.diary.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sipcount.diary.presentation.app.AppViewModel
import com.sipcount.diary.presentation.diary.DiaryViewModel
import com.sipcount.diary.presentation.game.GameViewModel
import com.sipcount.diary.presentation.history.PastDaysViewModel
import com.sipcount.diary.presentation.menu.MenuViewModel
import com.sipcount.diary.presentation.result.DayResultViewModel
import com.sipcount.diary.presentation.settings.SettingsViewModel
import com.sipcount.diary.presentation.splash.SplashViewModel
import com.sipcount.diary.presentation.types.DrinkTypesViewModel
import com.sipcount.diary.presentation.vessels.VesselRackViewModel
import com.sipcount.diary.presentation.week.WeekChartViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            AppViewModel(ServiceLocator.preferencesRepository)
        }
        initializer {
            SplashViewModel()
        }
        initializer {
            MenuViewModel(
                ServiceLocator.diaryRepository,
                ServiceLocator.vesselRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            GameViewModel(
                ServiceLocator.diaryRepository,
                ServiceLocator.vesselRepository,
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.addDrinkUseCase,
                ServiceLocator.removeDrinkUseCase,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            DayResultViewModel(
                ServiceLocator.diaryRepository,
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.vesselRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            DiaryViewModel(
                ServiceLocator.diaryRepository,
                ServiceLocator.vesselRepository,
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.removeDrinkUseCase,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            VesselRackViewModel(
                ServiceLocator.vesselRepository,
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            DrinkTypesViewModel(
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.diaryRepository
            )
        }
        initializer {
            WeekChartViewModel(
                ServiceLocator.diaryRepository,
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.buildWeekChartUseCase,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            PastDaysViewModel(
                ServiceLocator.diaryRepository,
                ServiceLocator.vesselRepository,
                ServiceLocator.drinkTypeRepository,
                ServiceLocator.preferencesRepository,
                ServiceLocator.removeDrinkUseCase,
                ServiceLocator.formatVolumeUseCase
            )
        }
        initializer {
            SettingsViewModel(
                ServiceLocator.preferencesRepository,
                ServiceLocator.diaryRepository,
                ServiceLocator.vesselRepository,
                ServiceLocator.diaryExporter,
                ServiceLocator.formatVolumeUseCase
            )
        }
    }
}
