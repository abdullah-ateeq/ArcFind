package com.example.arcfind.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = ArcTealDark,
    onPrimary = Color(0xFF00363D),
    primaryContainer = ArcTealContainerDark,
    onPrimaryContainer = ArcTealContainerLight,
    secondary = ArcSlateDark,
    onSecondary = Color(0xFF1B3438),
    tertiary = ArcAccentOrangeDark,
    onTertiary = Color(0xFF561F00),
    background = BackgroundDark,
    onBackground = Color(0xFFE1E3E4),
    surface = SurfaceDark,
    onSurface = Color(0xFFE1E3E4),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFBFC8CA),
    outline = OutlineDark,
    error = ErrorDark,
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = ArcTealLight,
    onPrimary = Color.White,
    primaryContainer = ArcTealContainerLight,
    onPrimaryContainer = Color(0xFF002024),
    secondary = ArcSlateLight,
    onSecondary = Color.White,
    tertiary = ArcAccentOrangeLight,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Color(0xFF191C1D),
    surface = SurfaceLight,
    onSurface = Color(0xFF191C1D),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3F484A),
    outline = OutlineLight,
    error = ErrorLight,
    onError = Color.White
)

@Composable
fun ArcFindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
