package com.pixel.vacstock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Fixed orange scheme rather than Android 12+ dynamic color, so the app looks the same on every
 * phone and on any future platform.
 */
@Composable
fun VacStockTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}

/** Fill for stock rows: white cards on the light background, one tone up from it in dark. */
internal val ColorScheme.stockRow: Color
    get() = if (surface.luminance() > 0.5f) surfaceContainerLowest else surfaceContainer
