package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
  primary = TerracottaOrange,
  onPrimary = Color.White,
  primaryContainer = TerracottaContainer,
  onPrimaryContainer = OnTerracottaContainer,
  secondary = ForestEmerald,
  onSecondary = Color.White,
  secondaryContainer = EmeraldContainer,
  onSecondaryContainer = OnEmeraldContainer,
  tertiary = RoyalGold,
  onTertiary = EspressoText,
  tertiaryContainer = GoldContainer,
  onTertiaryContainer = OnGoldContainer,
  background = WarmCream,
  onBackground = EspressoText,
  surface = WarmCardSurface,
  onSurface = EspressoText,
  surfaceVariant = WarmParchment,
  onSurfaceVariant = MutedCocoa,
  outline = Color(0xFFD6C4B0),
  error = CoralAlert,
  onError = Color.White,
  errorContainer = CoralContainer,
  onErrorContainer = Color(0xFF6B130E),
)

private val DarkColors = darkColorScheme(
  primary = Color(0xFFFF9859),
  onPrimary = Color(0xFF3E1800),
  primaryContainer = Color(0xFF6B310B),
  onPrimaryContainer = Color(0xFFFFE0CC),
  secondary = Color(0xFF72D4A5),
  onSecondary = Color(0xFF003823),
  secondaryContainer = Color(0xFF145238),
  onSecondaryContainer = Color(0xFFCFF7E2),
  tertiary = Color(0xFFFFD15C),
  onTertiary = Color(0xFF3B2900),
  tertiaryContainer = Color(0xFF5A4000),
  onTertiaryContainer = Color(0xFFFFECB8),
  background = DarkWoodBg,
  onBackground = DarkWarmCream,
  surface = DarkWoodSurface,
  onSurface = DarkWarmCream,
  surfaceVariant = Color(0xFF3B2E27),
  onSurfaceVariant = Color(0xFFD7C6B9),
  outline = Color(0xFF5B493F),
  error = Color(0xFFFF897D),
  onError = Color(0xFF5A0B07),
)

val AppShapes = Shapes(
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(18.dp),
  large = RoundedCornerShape(24.dp),
  extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun ShashkiTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = AppTypography,
    shapes = AppShapes,
    content = content,
  )
}
