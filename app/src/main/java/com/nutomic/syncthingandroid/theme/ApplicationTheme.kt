package com.nutomic.syncthingandroid.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PackLight = lightColorScheme(
    primary = Color(0xFF006674), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCCF5FA), onPrimaryContainer = Color(0xFF00363D),
    inversePrimary = Color(0xFF45E6F5),
    secondary = Color(0xFF805820), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE0B0), onSecondaryContainer = Color(0xFF3E2603),
    tertiary = Color(0xFF246858), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC4F1E2), onTertiaryContainer = Color(0xFF00382F),
    background = Color(0xFFF2F9FC), onBackground = Color(0xFF102A3A),
    surface = Color(0xFFF8FCFE), onSurface = Color(0xFF102A3A),
    surfaceVariant = Color(0xFFDCEBF0), onSurfaceVariant = Color(0xFF334E5A),
    surfaceTint = Color(0xFF006674),
    inverseSurface = Color(0xFF18303E), inverseOnSurface = Color(0xFFE3F6FC),
    outline = Color(0xFF536E79), outlineVariant = Color(0xFFB7CBD3),
    surfaceDim = Color(0xFFD0E0E8), surfaceBright = Color(0xFFF8FCFE),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFEDF6FA),
    surfaceContainer = Color(0xFFE7F1F6), surfaceContainerHigh = Color(0xFFDDEBF2),
    surfaceContainerHighest = Color(0xFFD3E5EE),
    error = Color(0xFFB3261E), onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
    scrim = Color(0xFF000000),
)

private val PackDark = darkColorScheme(
    primary = Color(0xFF45E6F5), onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004A56), onPrimaryContainer = Color(0xFFCFFBFF),
    inversePrimary = Color(0xFF006674),
    secondary = Color(0xFFFFB454), onSecondary = Color(0xFF432700),
    secondaryContainer = Color(0xFF5B3A10), onSecondaryContainer = Color(0xFFFFE2BA),
    tertiary = Color(0xFF98D9CA), onTertiary = Color(0xFF00382F),
    tertiaryContainer = Color(0xFF155044), onTertiaryContainer = Color(0xFFC4F1E2),
    background = Color(0xFF08121F), onBackground = Color(0xFFE3F6FC),
    surface = Color(0xFF0E1C2C), onSurface = Color(0xFFE3F6FC),
    surfaceVariant = Color(0xFF20394A), onSurfaceVariant = Color(0xFFC2DDE8),
    surfaceTint = Color(0xFF45E6F5),
    inverseSurface = Color(0xFFE3F6FC), inverseOnSurface = Color(0xFF102A3A),
    outline = Color(0xFF80ADBF), outlineVariant = Color(0xFF36566A),
    surfaceDim = Color(0xFF08121F), surfaceBright = Color(0xFF243D50),
    surfaceContainerLowest = Color(0xFF050D17), surfaceContainerLow = Color(0xFF0B1725),
    surfaceContainer = Color(0xFF0E1C2C), surfaceContainerHigh = Color(0xFF14283A),
    surfaceContainerHighest = Color(0xFF1C3448),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
    scrim = Color(0xFF000000),
)

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = if (darkTheme) PackDark else PackLight, content = content)
}
