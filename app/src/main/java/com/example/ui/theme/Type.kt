package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val ComfortaaFontFamily = FontFamily(
  Font(R.font.comfortaa, FontWeight.Normal),
  Font(R.font.comfortaa, FontWeight.Bold),
)

val NunitoFontFamily = FontFamily(
  Font(R.font.nunito, FontWeight.Normal),
  Font(R.font.nunito, FontWeight.SemiBold),
  Font(R.font.nunito, FontWeight.Bold),
)

val AppTypography = Typography(
  displaySmall = TextStyle(
    fontFamily = ComfortaaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 30.sp,
    lineHeight = 36.sp,
  ),
  headlineMedium = TextStyle(
    fontFamily = ComfortaaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
  ),
  headlineSmall = TextStyle(
    fontFamily = ComfortaaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
  ),
  titleLarge = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 19.sp,
    lineHeight = 25.sp,
  ),
  titleMedium = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
  ),
  bodyLarge = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 23.sp,
  ),
  bodyMedium = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
  ),
  labelLarge = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 18.sp,
  ),
  labelMedium = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
  ),
)
