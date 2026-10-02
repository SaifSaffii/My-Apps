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

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoPrimaryLight,
    secondary = AmberSecondaryLight,
    onSecondary = Color.Black,
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = AmberSecondaryLight,
    tertiary = TealTertiaryLight,
    onTertiary = Color.Black,
    tertiaryContainer = TealContainerDark,
    onTertiaryContainer = TealTertiaryLight,
    background = BackgroundDark,
    onBackground = Color(0xFFF1F5F9),
    surface = CardBackgroundDark,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = IndigoPrimaryDark,
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = AmberSecondary,
    tertiary = TealTertiary,
    onTertiary = Color.White,
    tertiaryContainer = TealContainerLight,
    onTertiaryContainer = TealTertiary,
    background = BackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = CardBackgroundLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
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
