package com.kavyakanaja.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
  primary = GoldenAccent,
  secondary = DeepSaffron,
  background = DarkBackground,
  surface = SurfaceDark,
  onPrimary = InkBrown,
  onSecondary = TextPrimary,
  onBackground = TextPrimary,
  onSurface = TextPrimary,
  surfaceVariant = CardSurface,
  onSurfaceVariant = TextSecondary
)

private val LightColors = lightColorScheme(
  primary = DeepSaffron,
  secondary = GoldenAccent,
  background = ParchmentCream,
  surface = ColorLike.Parchment,
  onPrimary = TextPrimary,
  onSecondary = InkBrown,
  onBackground = InkBrown,
  onSurface = InkBrown,
  surfaceVariant = ColorLike.LightCard,
  onSurfaceVariant = DeepSaffron
)

private object ColorLike {
  val Parchment = ParchmentCream
  val LightCard = androidx.compose.ui.graphics.Color(0xFFFFF8E8)
}

@Composable
fun KavyaKanajaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = AppTypography,
    content = content
  )
}
