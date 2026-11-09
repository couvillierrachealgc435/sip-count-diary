package com.sipcount.diary.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sipcount.diary.core.config.GameConfig
import com.sipcount.diary.core.navigation.Screen
import com.sipcount.diary.core.theme.AppTheme
import com.sipcount.diary.core.theme.LocalAppPalette
import com.sipcount.diary.presentation.diary.DiaryScreen
import com.sipcount.diary.presentation.game.TodayGlassScreen
import com.sipcount.diary.presentation.history.PastDaysScreen
import com.sipcount.diary.presentation.menu.MenuScreen
import com.sipcount.diary.presentation.result.DayResultScreen
import com.sipcount.diary.presentation.settings.SettingsScreen
import com.sipcount.diary.presentation.splash.SplashScreen
import com.sipcount.diary.presentation.types.DrinkTypesScreen
import com.sipcount.diary.presentation.vessels.VesselRackScreen
import com.sipcount.diary.presentation.week.WeekChartScreen
import com.sipcount.diary.core.di.AppViewModelFactory

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()
    val darkTheme by appViewModel.darkTheme.collectAsStateWithLifecycle()

    AppTheme(darkTheme = darkTheme) {
        val palette = LocalAppPalette.current
        BackHandler(enabled = canGoBack) {
            appViewModel.back()
        }
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(animationSpec = tween(GameConfig.SCREEN_FADE_MS)) togetherWith
                    fadeOut(animationSpec = tween(350))
            },
            label = "screenTransition",
            modifier = Modifier
                .fillMaxSize()
                .background(palette.backgroundTop)
        ) { target ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (target) {
                    Screen.Splash -> SplashScreen(
                        onFinished = { appViewModel.replaceWith(Screen.Menu) }
                    )

                    Screen.Menu -> MenuScreen(
                        onStart = { appViewModel.navigate(Screen.TodayGlass) },
                        onOpen = { appViewModel.navigate(it) }
                    )

                    Screen.TodayGlass -> TodayGlassScreen(
                        onBack = { appViewModel.backToMenu() },
                        onCloseDay = { appViewModel.navigate(Screen.DayResult(it)) },
                        onManageVessels = { appViewModel.navigate(Screen.VesselRack) }
                    )

                    is Screen.DayResult -> DayResultScreen(
                        epochDay = target.epochDay,
                        onAgain = { appViewModel.navigate(Screen.TodayGlass) },
                        onMenu = { appViewModel.backToMenu() }
                    )

                    Screen.Diary -> DiaryScreen(
                        onBack = { appViewModel.back() },
                        onStart = { appViewModel.navigate(Screen.TodayGlass) }
                    )

                    Screen.VesselRack -> VesselRackScreen(
                        onBack = { appViewModel.back() }
                    )

                    Screen.DrinkTypes -> DrinkTypesScreen(
                        onBack = { appViewModel.back() }
                    )

                    Screen.WeekChart -> WeekChartScreen(
                        onBack = { appViewModel.back() }
                    )

                    Screen.PastDays -> PastDaysScreen(
                        onBack = { appViewModel.back() }
                    )

                    Screen.Settings -> SettingsScreen(
                        onBack = { appViewModel.back() }
                    )
                }
            }
        }
    }
}
