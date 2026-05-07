package com.financetracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.financetracker.data.local.prefs.UserPreferences

// Light theme — teal seed
private fun lightColorScheme(accent: AccentColor = AccentColor.TEAL): ColorScheme = lightColorScheme(
    primary = accent.primaryColor,
    onPrimary = Color.White,
    primaryContainer = accent.primaryContainerColor,
    onPrimaryContainer = Teal10,
    secondary = TealSecondary40,
    onSecondary = Color.White,
    secondaryContainer = TealSecondary90,
    onSecondaryContainer = TealSecondary10,
    tertiary = Tertiary40,
    onTertiary = Color.White,
    tertiaryContainer = Tertiary90,
    onTertiaryContainer = Tertiary10,
    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Error10,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = Neutral95,
    onSurfaceVariant = Neutral30,
    outline = Neutral80,
    outlineVariant = Neutral90,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    scrim = Color.Black.copy(alpha = 0.32f)
)

// Dark theme
private fun darkColorScheme(accent: AccentColor = AccentColor.TEAL): ColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal20,
    primaryContainer = Teal30,
    onPrimaryContainer = Teal90,
    secondary = TealSecondary80,
    onSecondary = TealSecondary20,
    secondaryContainer = TealSecondary30,
    onSecondaryContainer = TealSecondary90,
    tertiary = Tertiary80,
    onTertiary = Tertiary20,
    tertiaryContainer = Tertiary30,
    onTertiaryContainer = Tertiary90,
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral80,
    outline = Neutral40,
    outlineVariant = Neutral30,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral10,
    scrim = Color.Black.copy(alpha = 0.32f)
)

// OLED Black theme
private fun oledColorScheme(accent: AccentColor = AccentColor.TEAL): ColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Color.Black,
    primaryContainer = Teal30,
    onPrimaryContainer = Teal90,
    secondary = TealSecondary80,
    onSecondary = Color.Black,
    secondaryContainer = TealSecondary30,
    onSecondaryContainer = TealSecondary90,
    tertiary = Tertiary80,
    onTertiary = Color.Black,
    tertiaryContainer = Tertiary30,
    onTertiaryContainer = Tertiary90,
    error = Error80,
    onError = Color.Black,
    errorContainer = Error30,
    onErrorContainer = Error90,
    background = Color.Black,
    onBackground = Neutral90,
    surface = Color.Black,
    onSurface = Neutral90,
    surfaceVariant = Color(0xFF1A1C1C),
    onSurfaceVariant = Neutral80,
    outline = Neutral40,
    outlineVariant = Color(0xFF1A1C1C),
    inverseSurface = Neutral90,
    inverseOnSurface = Color.Black,
    scrim = Color.Black.copy(alpha = 0.32f)
)

@Composable
fun FinanceTrackingAppTheme(
    themeMode: Int = UserPreferences.THEME_LIGHT,
    accentColor: Int = 0,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val accent = AccentColor.entries.getOrElse(accentColor) { AccentColor.TEAL }
    val darkTheme = when (themeMode) {
        UserPreferences.THEME_DARK -> true
        UserPreferences.THEME_OLED -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when (themeMode) {
        UserPreferences.THEME_OLED -> oledColorScheme(accent)
        UserPreferences.THEME_DARK -> darkColorScheme(accent)
        else -> lightColorScheme(accent)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (themeMode == UserPreferences.THEME_OLED) {
                Color.Black.toArgb()
            } else {
                colorScheme.surface.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
