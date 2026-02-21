package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun applyTheme()
    fun isDarkTheme(): Boolean
}