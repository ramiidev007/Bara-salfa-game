package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = NeonGreen,
  onPrimary = DarkBackground,
  primaryContainer = NeonPurple,
  onPrimaryContainer = TextPrimary,
  secondary = NeonCyan,
  onSecondary = DarkBackground,
  tertiary = NeonPink,
  onTertiary = TextPrimary,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = DarkSurfaceBorder,
  error = NeonRed,
  onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

