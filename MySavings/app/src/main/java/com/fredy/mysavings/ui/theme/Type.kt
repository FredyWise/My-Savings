package com.fredy.mysavings.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val fontFamily1: FontFamily = FontFamily.Default
val fontFamily2: FontFamily = FontFamily.Default
val fontWeight1: FontWeight = FontWeight.Normal
val fontWeight2: FontWeight = FontWeight.Medium
val fontWeight3: FontWeight = FontWeight.SemiBold

val typography = Typography(

    displayLarge = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight3,
        fontSize = 57.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),headlineSmall = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight3,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ), titleLarge = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight1,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ), titleMedium = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight2,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ), bodyLarge = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight1,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ), bodyMedium = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight2,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ), labelMedium = TextStyle(
        fontFamily = fontFamily1,
        fontWeight = fontWeight3,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)