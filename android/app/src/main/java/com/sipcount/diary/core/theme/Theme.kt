package com.sipcount.diary.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppPalette(
    val backgroundTop: Color,
    val backgroundMid: Color,
    val backgroundDeep: Color,
    val surface: Color,
    val surfaceAlt: Color,
    val surfaceTranslucent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val outline: Color,
    val isDark: Boolean
)

private val LightPalette = AppPalette(
    backgroundTop = BackgroundTop,
    backgroundMid = BackgroundMid,
    backgroundDeep = BackgroundDeep,
    surface = SurfaceWhite,
    surfaceAlt = SurfaceAlt,
    surfaceTranslucent = SurfaceTranslucent,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    outline = OutlineBlue,
    isDark = false
)

private val DarkPalette = AppPalette(
    backgroundTop = DarkBackgroundTop,
    backgroundMid = DarkBackgroundMid,
    backgroundDeep = DarkBackgroundDeep,
    surface = DarkSurface,
    surfaceAlt = DarkSurfaceAlt,
    surfaceTranslucent = DarkSurfaceTranslucent,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    outline = PrimaryLight,
    isDark = true
)

val LocalAppPalette: ProvidableCompositionLocal<AppPalette> =
    staticCompositionLocalOf { LightPalette }

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDeep,
    secondary = Teal,
    onSecondary = Color.White,
    tertiary = Accent,
    onTertiary = Color.White,
    tertiaryContainer = AccentSoft,
    onTertiaryContainer = TextPrimary,
    background = BackgroundTop,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = OutlineBlue,
    outlineVariant = BackgroundDeep,
    error = Accent,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Color(0xFF04253A),
    primaryContainer = PrimaryDeep,
    onPrimaryContainer = BackgroundTop,
    secondary = Teal,
    onSecondary = Color(0xFF04253A),
    tertiary = Accent,
    onTertiary = Color(0xFF3A0C19),
    tertiaryContainer = Color(0xFF6E2B3D),
    onTertiaryContainer = AccentSoft,
    background = DarkBackgroundTop,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceAlt,
    onSurfaceVariant = DarkTextSecondary,
    outline = PrimaryLight,
    outlineVariant = DarkBackgroundDeep,
    error = Accent,
    onError = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppPalette provides if (darkTheme) DarkPalette else LightPalette
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = AppTypography,
            content = content
        )
    }
}
