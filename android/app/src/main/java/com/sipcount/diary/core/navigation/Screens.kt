package com.sipcount.diary.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object TodayGlass : Screen
    data class DayResult(val epochDay: Long) : Screen
    data object Diary : Screen
    data object VesselRack : Screen
    data object DrinkTypes : Screen
    data object WeekChart : Screen
    data object PastDays : Screen
    data object Settings : Screen
}
