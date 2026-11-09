package com.sipcount.diary.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val RoundSans = FontFamily.SansSerif

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W800,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W800,
        fontSize = 46.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W800,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 2.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W800,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W700,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W700,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.4.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W700,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W400,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W600,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.6.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W700,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        letterSpacing = 1.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RoundSans,
        fontWeight = FontWeight.W700,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 2.sp
    )
)
