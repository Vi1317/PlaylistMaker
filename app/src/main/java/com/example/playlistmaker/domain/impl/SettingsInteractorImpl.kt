package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl (private val settingsRepository: SettingsRepository) : SettingsInteractor{
    override fun switchTheme(darkThemeEnabled: Boolean) {
        return settingsRepository.switchTheme(darkThemeEnabled)
    }
    override fun applyTheme() {
        return settingsRepository.applyTheme()
    }
    override fun isDarkTheme(): Boolean {
        return settingsRepository.isDarkTheme()
    }

}