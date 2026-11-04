package com.nutomic.syncthingandroid.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PackLight = lightColorScheme(
    primary = Color(0xFF006674),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDAFAFD),
    onPrimaryContainer = Color(0xFF006674),
    secondary = Color(0xFF996C32),
    secondaryContainer = Color(0xFFFFECD4),
    onSecondaryContainer = Color(0xFF4C3619),
)

private val PackDark = darkColorScheme(
    primary = Color(0xFF45E6F5),
    background = Color(0xFF08121F),
    surface = Color(0xFF0E1C2C),
    onBackground = Color(0xFFE3F6FC),
    onSurface = Color(0xFFE3F6FC),
    onPrimary = Color(0xFF006674),
    primaryContainer = Color(0xFF006674),
    onPrimaryContainer = Color(0xFFE3FBFE),
    secondary = Color(0xFFFFCA87),
    secondaryContainer = Color(0xFF735126),
    onSecondaryContainer = Color(0xFFFFF0DD),
)

@Composable
fun ApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) PackDark else PackLight,
        content = content
    )
}
