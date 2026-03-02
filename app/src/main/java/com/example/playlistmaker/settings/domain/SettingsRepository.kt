package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.data.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}