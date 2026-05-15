package com.kavyakanaja.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val KannadaFont = FontFamily.Serif
val EnglishFont = FontFamily.Serif
val DisplayFont = FontFamily.Serif

val AppTypography = Typography(
  displayLarge = TextStyle(
    fontFamily = KannadaFont,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp
  ),
  headlineMedium = TextStyle(
    fontFamily = DisplayFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = EnglishFont,
    fontSize = 16.sp,
    lineHeight = 24.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = KannadaFont,
    fontSize = 15.sp,
    lineHeight = 24.sp
  ),
  labelSmall = TextStyle(
    fontFamily = EnglishFont,
    fontStyle = FontStyle.Italic,
    fontSize = 12.sp
  )
)
