package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl (private val storage: ThemeStorage) :
    SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return storage.getData()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.storeData(settings)
    }
}