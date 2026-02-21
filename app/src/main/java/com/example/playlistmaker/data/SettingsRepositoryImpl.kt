package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.SettingsManager
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl (private val settingsManager : SettingsManager) : SettingsRepository {
    override fun switchTheme(darkThemeEnabled: Boolean) {
        settingsManager.switchTheme(darkThemeEnabled)
    }
    override fun applyTheme() {
        settingsManager.applyTheme()
    }
    override fun isDarkTheme(): Boolean {
        return settingsManager.isDarkTheme()
    }
}