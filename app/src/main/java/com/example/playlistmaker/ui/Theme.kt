package com.example.playlistmaker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = White,
    primaryContainer = TextGray,
    onPrimary = Black,
    secondary = Blue,
    secondaryContainer = GrayBtnLight,
    onSecondary = TextGray
)

private val DarkColors = darkColorScheme(
    primary = Black,
    primaryContainer = Black,
    onPrimary = White,
    secondary = Black,
    secondaryContainer = GrayBtnNight,
    onSecondary = White
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
