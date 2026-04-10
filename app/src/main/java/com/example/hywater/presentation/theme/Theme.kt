package com.example.hywater.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = BlueGraySecondary,
    onSecondary = BlueGrayOnSecondary,
    secondaryContainer = BlueGraySecondaryContainer,
    onSecondaryContainer = BlueGrayOnSecondaryContainer,
    error = ErrorRed,
    errorContainer = ErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryContainer,
    onPrimary = TealOnPrimaryContainer,
    primaryContainer = TealPrimary,
    onPrimaryContainer = TealPrimaryContainer,
    secondary = BlueGraySecondaryContainer,
    onSecondary = BlueGrayOnSecondaryContainer
)

@Composable
fun HyWaterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is supported on Android 12+ and uses the wallpaper palette
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HyWaterTypography,
        content = content
    )
}
