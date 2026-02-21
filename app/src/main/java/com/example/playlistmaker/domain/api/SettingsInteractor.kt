package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun switchTheme(darkThemeEnabled: Boolean)
    fun applyTheme()
    fun isDarkTheme(): Boolean
}