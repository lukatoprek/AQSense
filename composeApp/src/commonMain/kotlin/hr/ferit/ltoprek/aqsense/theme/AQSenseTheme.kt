package hr.ferit.ltoprek.aqsense.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val lightColorTheme = lightColorScheme(
    primary = primary_light,
    onPrimary = onPrimary_light,
    primaryContainer = primaryContainer_light,
    onPrimaryContainer = onPrimaryContainer_light,
    secondary = secondary_light,
    onSecondary = onSecondary_light,
    secondaryContainer = secondaryContainer_light,
    onSecondaryContainer = onSecondaryContainer_light,
    tertiary = tertiary_light,
    onTertiary = onTertiary_light,
    tertiaryContainer = tertiaryContainer_light,
    onTertiaryContainer = onTertiaryContainer_light,
    error = error_light,
    background = background_light,
    onBackground = onBackground_light,
    surface = surface_light,
    onSurface = onSurface_light,
)

val DarkColorScheme = darkColorScheme(
    primary = primary_dark,
    onPrimary = onPrimary_dark,
    primaryContainer = primaryContainer_dark,
    onPrimaryContainer = onPrimaryContainer_dark,
    secondary = secondary_dark,
    onSecondary = onSecondary_dark,
    secondaryContainer = secondaryContainer_dark,
    onSecondaryContainer = onSecondaryContainer_dark,
    tertiary = tertiary_dark,
    onTertiary = onTertiary_dark,
    tertiaryContainer = tertiaryContainer_dark,
    onTertiaryContainer = onTertiaryContainer_dark,
    error = error_dark,
    background = background_dark,
    onBackground = onBackground_dark,
    surface = surface_dark,
    onSurface = onSurface_dark,
)

@Composable
fun AQSenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> lightColorTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}