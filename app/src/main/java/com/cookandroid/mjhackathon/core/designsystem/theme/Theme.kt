package com.cookandroid.mjhackathon.core.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MjHackathonTheme(
    colors: MjHackathonColors = defaultMjHackathonColors,
    typography: MjHackathonTypography = defaultMjHackathonTypography,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    CompositionLocalProvider(
        LocalMjHackathonColorProvider provides colors,
        LocalMjHackathonTypographyProvider provides typography
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object MjHackathonTheme {
    val colors: MjHackathonColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMjHackathonColorProvider.current
    val typography: MjHackathonTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalMjHackathonTypographyProvider.current
}
