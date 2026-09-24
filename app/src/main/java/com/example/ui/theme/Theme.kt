package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CrimsonPrimaryDark,
    onPrimary = Color(0xFF380002),
    primaryContainer = Color(0xFF5E0B0B),
    onPrimaryContainer = Color(0xFFFFDADA),
    secondary = GoldAntiqueDark,
    onSecondary = Color(0xFF382600),
    secondaryContainer = Color(0xFF3B2F17),
    onSecondaryContainer = Color(0xFFFFE0A1),
    surface = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    background = BackgroundWarmDark,
    onBackground = TextPrimaryDark,
    outlineVariant = BorderSubtleDark,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CrimsonPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDE8E6),
    onPrimaryContainer = Color(0xFF4A0004),
    secondary = GoldAntiqueLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBF2DC),
    onSecondaryContainer = Color(0xFF382600),
    surface = SurfaceCardLight,
    onSurface = TextPrimaryCharcoal,
    surfaceVariant = SurfaceVariantWarm,
    onSurfaceVariant = TextSecondaryMuted,
    background = BackgroundCreamLight,
    onBackground = TextPrimaryCharcoal,
    outlineVariant = BorderSubtleLight,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = RedBadgeLight,
    onErrorContainer = Color(0xFF410002)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
